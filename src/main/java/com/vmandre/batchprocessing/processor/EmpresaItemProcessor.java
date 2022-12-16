package com.vmandre.batchprocessing.processor;

import com.vmandre.batchprocessing.model.Empresa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EmpresaItemProcessor implements ItemProcessor<Empresa, Empresa> {

    private Set<Empresa> seenEmpresas;

    public EmpresaItemProcessor() {
        this.seenEmpresas = new HashSet<>();
    }

    @Override
    public Empresa process(Empresa empresa) {

        // ignore duplicated empresa in the file
        if (seenEmpresas.contains(empresa)) {
            return null;
        }
        seenEmpresas.add(empresa);

        final Empresa transformedEmpresa = new Empresa(empresa.getCnpj(), empresa.getNome());

        log.info(String.format("Converting (%s) into (%s)", empresa, transformedEmpresa));

        return transformedEmpresa;
    }
}
