package com.vmandre.batchprocessing.listener;

import com.vmandre.batchprocessing.model.Empresa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate.query("SELECT id, cnpj, nome FROM empresa_credito_privado",
                    (rs, rowNum) -> new Empresa(
                            rs.getLong("id"),
                            rs.getString("cnpj"),
                            rs.getString("nome"))
            ).forEach(empresa -> log.info(String.format("Found <%s> in the database", empresa)));
        }
    }
}
