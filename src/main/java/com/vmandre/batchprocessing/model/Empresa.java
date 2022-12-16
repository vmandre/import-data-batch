package com.vmandre.batchprocessing.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Empresa {

    private Long id;
    @NonNull
    private String cnpj;
    @NonNull
    private String nome;
}
