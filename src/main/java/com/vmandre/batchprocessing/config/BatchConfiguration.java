package com.vmandre.batchprocessing.config;

import com.vmandre.batchprocessing.listener.JobCompletionNotificationListener;
import com.vmandre.batchprocessing.model.Empresa;
import com.vmandre.batchprocessing.processor.EmpresaItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    @Bean
    public FlatFileItemReader<Empresa> reader() {
        DelimitedLineTokenizer dlt = new DelimitedLineTokenizer(",");
        dlt.setNames(new String[]{"data_analise", "cnpj", "nome", "tipo_ativo", "ticker", "indexador", "tipo_indexador", "percent_indexador", "data_vencimento"});
        return new FlatFileItemReaderBuilder<Empresa>()
                .name("empresaItemReader")
                .resource(new ClassPathResource("privado_DEB.csv"))
                .linesToSkip(1) // file header
                .delimited()
                .includedFields(1, 2) // indexes of the columns in the file
                .names(new String[]{"cnpj", "nome"})// name of the attribute in Empresa class
                .targetType(Empresa.class)
                .build();
    }

    @Bean
    public EmpresaItemProcessor processor() {
        return new EmpresaItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Empresa> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Empresa>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO empresa_credito_privado (cnpj, nome) VALUES(:cnpj, :nome)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importEmpresaJob(JobRepository jobRepository,
                                JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("importEmpresaJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Empresa> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Empresa, Empresa>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
