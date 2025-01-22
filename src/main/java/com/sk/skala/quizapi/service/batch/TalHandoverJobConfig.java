package com.sk.skala.quizapi.service.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.sk.skala.quizapi.data.batch.TalP1005010128Csv;
import com.sk.skala.quizapi.data.table.TalHandover;
import com.sk.skala.quizapi.data.table.TalHandoverId;
import com.sk.skala.quizapi.repository.TalHandoverRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class TalHandoverJobConfig {

	private final TalHandoverRepository talHandoverRepository;

	private final String CSV_FILES = "file:D:/data/DPSX_5G_SS_RAN_ACPF_60M/*.csv";
	private final int CHUNK_SIZE = 50;

	ItemReader<TalP1005010128Csv> talP1005010128CsvReader(Resource resource) {
		log.debug("talP1005010128CsvReader: resource={}", resource.getFilename());

		return new FlatFileItemReaderBuilder<TalP1005010128Csv>().name("flatFileItemReader").resource(resource)
				.delimited().delimiter(",").names(TalP1005010128Csv.FIELDS)
				.fieldSetMapper(new TalP1005010128Csv.CsvFieldSetMapper()).encoding("EUC-KR").build();
	}

	ItemProcessor<TalP1005010128Csv, TalHandover> talHandoverProcessor(Resource resource) {

		return talP1005010128Csv -> {
			TalHandover talHandover = new TalHandover();
			talHandover.setId(
					new TalHandoverId(new Date(), resource.getFilename(), talP1005010128Csv.getFieldLong("talId")));
			talHandover.setAttTotCnt(talP1005010128Csv.getFieldLong("attTotCnt"));
			talHandover.setIntraAttTot(talP1005010128Csv.getFieldLong("intraAttTot"));
			talHandover.setNbTacList(talP1005010128Csv.getNbTacList());
			return talHandover;
		};
	}

	ItemWriter<TalHandover> talHandoverDataWriter() {

		return items -> {
			log.debug("talHandoverDataWriter: chunk-size={}", items.size());
			talHandoverRepository.saveAll(items);
		};
	}

	Step talHandoverJobStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			Resource resource) {
		log.debug("talHandoverJobStep: resource={}", resource.getFilename());

		return new StepBuilder("talHandoverJobStep", jobRepository)
				.<TalP1005010128Csv, TalHandover>chunk(CHUNK_SIZE, transactionManager)
				.reader(talP1005010128CsvReader(resource)).processor(talHandoverProcessor(resource))
				.writer(talHandoverDataWriter()).build();
	}

	@Bean
	@JobScope
	Flow parallelStepsFlow(JobRepository jobRepository, PlatformTransactionManager transactionManager)
			throws Exception {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(CSV_FILES);
		log.debug("parallelStepsFlow: resources.length={}", resources.length);
		if (resources.length == 0) {
			throw new IllegalStateException("No CSV files found for reading.");

		}

		List<Flow> flows = new ArrayList<>();
		for (Resource resource : resources) {
			Flow flow = new FlowBuilder<Flow>("flow-" + resource.getFilename())
					.start(talHandoverJobStep(jobRepository, transactionManager, resource)).build();

			flows.add(flow);
		}

		return new FlowBuilder<Flow>("parallelStepsFlow").split(new SimpleAsyncTaskExecutor())
				.add(flows.toArray(new Flow[0])).build();
	}

	@Bean
	Step renameFilesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("renameFilesStep", jobRepository).tasklet((contribution, chunkContext) -> {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources(CSV_FILES);

			log.debug("renameFilesStep: resources.length={}", resources.length);

			for (Resource resource : resources) {
				File file = resource.getFile();
				File renamedFile = new File(file.getAbsolutePath().replace(".csv", ".fin"));
				file.renameTo(renamedFile);
			}
			return RepeatStatus.FINISHED;
		}, transactionManager).build();
	}

	@Bean
	Job talHandoverJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
		return new JobBuilder("talHandoverJob", jobRepository)
				.start(parallelStepsFlow(jobRepository, transactionManager)).end().build();
//		.next(renameFilesStep(jobRepository, transactionManager)).end().build();
	}
}
