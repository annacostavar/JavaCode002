package com.br.var.solutions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/Pessoa")
@CrossOrigin(origins = "*")
@Slf4j
public class PessoaController {
//    1                   2                   3              4
//public/privado // tipo de retorno // nome do método // parâmetros
//Endpoint

    @GetMapping
    public ResponseEntity<Object> get() {

        ObjetoRequest Pessoa = new ObjetoRequest();
        Pessoa.setNome("Anna");
        Pessoa.setSobrenome("Costa");
        Pessoa.setPeso(61);
//        Identidade.setAltura(1.61);

        return ResponseEntity.ok(Pessoa);
    }

    @GetMapping("/resumo")
    public ResponseEntity<Object> getPessoa(@RequestBody ObjetoRequest pessoinha, @RequestParam(value = "valida_mundial") Boolean DesejaValidarMundial) {
        IformacoesIMC imc = new IformacoesIMC();
        int anoNascimento = 0;
        String impostoRenda = null;
        String validaMundial = null;
        String saldoEmDolar = null;

//        Object resumo = null;
        if (!pessoinha.getNome().isEmpty()) {
            log.info("Iniciando o processo de resumo da pessoa", pessoinha);

            if (Objects.nonNull(pessoinha.getPeso()) && Objects.nonNull(pessoinha.getAltura())) {
                log.info("Iniciando o calculo do IMC");
                imc = calcularImc(pessoinha.getPeso(), pessoinha.getAltura());
            }
            if (Objects.nonNull(pessoinha.getIdade())) {

                log.info("Iniciando o calculo do ano de nascimento");
                anoNascimento = calcularNascimento(pessoinha.getIdade());
            }
            if (Objects.nonNull(pessoinha.getSalario()) && Objects.nonNull(pessoinha.getAliquota())) {
                log.info("Iniciando o calculo de Imposto de Renda");
                impostoRenda = calcularFaixaImpostoRenda(pessoinha.getSalario());
            }
            if (Boolean.TRUE.equals(DesejaValidarMundial)) {
                if (Objects.nonNull(pessoinha.getTime())) {
                    log.info("Validando de o time do coração tem mundial");
                    validaMundial = calcularMundial(pessoinha.getTime());
                }
            }

            if (Objects.nonNull(pessoinha.getSaldo())) {
                log.info("Convertendo o real em dolar");
                saldoEmDolar = converterRealEmDolar(pessoinha.getSaldo());
            }

            log.info("Montando Objeto de retorno para o front-end.");
            PessoaResponse resumo = montarRespostaFrontEnd(pessoinha, imc, anoNascimento, impostoRenda, validaMundial, saldoEmDolar);
            return ResponseEntity.ok(resumo);
        }

        return ResponseEntity.noContent().build();
    }

    private String converterRealEmDolar(double saldo) {

        return String.valueOf(saldo / 5.25);
    }


    private PessoaResponse montarRespostaFrontEnd(ObjetoRequest pessoa, IformacoesIMC imc, int anoNascimento,
                                                  String impostoRenda, String validaMundial, String saldoEmDolar) {
        PessoaResponse response = new PessoaResponse();

        response.setNome(pessoa.getNome());
        response.setImc(imc.getImc());
        response.setClassificacaoIMC(imc.getClassificacao());
        response.setSalario(impostoRenda);
        response.setAnoNascimento(anoNascimento);
        response.setMundialClubes(validaMundial);
        response.setSaldoEmDolar(saldoEmDolar);
        response.setIdade(pessoa.getIdade());
        response.setTime(pessoa.getTime());
        response.setSobrenome(pessoa.getSobrenome());
        response.setAltura(pessoa.getAltura());
        response.setPeso(pessoa.getPeso());
        response.setSaldo(pessoa.getSaldo());

        return response;
    }


    //    Regra: Base de calculo é: Salário Bruto x Aliquota - dedução.
    private String calcularFaixaImpostoRenda(double salario) {
        log.info("Iniciando o calculo do imposto de renda: " + salario);
        String novoSalarioCalculado;

        if (salario <= 1903.98) {
            return "Insento";

        } else if (salario <= 1093.99 && salario > 2826.65) {

            double calculoIRF = 142.80 - ((salario * 0.075) / 100);
            double novoSalario = salario - calculoIRF;
            novoSalarioCalculado = String.valueOf(novoSalario);
            return novoSalarioCalculado;
        } else if (salario <= 2826.66 && salario > 3751.05) {

            double calculoIRF = 354.80 - ((salario * 0.15) / 100);
            double novoSalario = salario - calculoIRF;
            novoSalarioCalculado = String.valueOf(novoSalario);
            return novoSalarioCalculado;

        } else if (salario <= 3751.06 && salario > 4664.68) {

            double calculoIRF = 636.13 - ((salario * 0.225) / 100);
            double novoSalario = salario - calculoIRF;
            novoSalarioCalculado = String.valueOf(novoSalario);
            return novoSalarioCalculado;

        } else {

            double calculoIRF = 869.36 - ((salario * 275) / 100);
            double novoSalario = salario - calculoIRF;
            novoSalarioCalculado = String.valueOf(novoSalario);
            return novoSalarioCalculado;
        }
    }

    private int calcularNascimento(int idade) {
        LocalDate datalocal = LocalDate.now();
        int anoAtual = datalocal.getYear();
        return anoAtual - idade;
    }

    private IformacoesIMC calcularImc(double peso, double altura) {
        double imc = peso / (altura * altura);

        IformacoesIMC imcCalculado = new IformacoesIMC();

        if (imc < 18.5) {
            imcCalculado.setImc(String.valueOf(imc));
            imcCalculado.setClassificacao(String.valueOf("Abaixo do peso"));
            return imcCalculado;
        } else if (imc >= 18.5 && imc <= 24.9) {
            imcCalculado.setImc(String.valueOf(imc));
            imcCalculado.setClassificacao(String.valueOf("Peso ideal"));
            return imcCalculado;
        } else if (imc < 24.9 && imc >= 29.9) {
            imcCalculado.setImc(String.valueOf(imc));
            imcCalculado.setClassificacao(String.valueOf("Acima do peso"));
            return imcCalculado;
        } else if (imc > 29.9 && imc <= 34.9) {
            imcCalculado.setImc(String.valueOf(imc));
            imcCalculado.setClassificacao(String.valueOf("Obesidade classe I"));
            return imcCalculado;
        } else if (imc >= 35.0 && imc <= 39.9) {
            imcCalculado.setImc(String.valueOf(imc));
            imcCalculado.setClassificacao(String.valueOf("Obesidade classe II"));
            return imcCalculado;
        } else {
            imcCalculado.setImc(String.valueOf(imc));
            imcCalculado.setClassificacao(String.valueOf("Obesidade classe III"));
            return imcCalculado;
        }
    }

    private String calcularMundial(String time) {

        if (time.equalsIgnoreCase("Corinthias")) {
            return "Parabéns, seu time possui 2 mundiais de clubes conforme a FIFA.";
        } else if (time.equalsIgnoreCase("São Paulo")) {
            return "Parabéns, seu time possui 3 mundiais de clubes conforme a FIFA.";
        } else if (time.equalsIgnoreCase("Santos")) {
            return "Parabéns, seu time possui 2 mundiais de clubes conforme a FIFA.";
        } else {
            return "Poxa, que pena, continue torcendo para o seu time ganhar o mundial.";
        }
    }
}



