package projetolapr1_iteracao2;

import java.io.File;
import java.util.Scanner;
import java.util.Formatter;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class MetodoTOPSIS {

    public static Calendar Data = Calendar.getInstance();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd_MM_yyyy HH.mm.ss");/*Windows nao permite : como caracter*/
    private static final String FILE_LOG_ERROS = SDF.format(Data.getTime()) + ".txt";
    private final static String FILE_LOG_ERROS_Dados1 = "Identificadores de linha não encontrados";
    private final static String FILE_LOG_ERROS_Dados2 = "Identificadores de vetor pesos incompletos";
    private final static String FILE_LOG_ERROS_Dados3 = "Numero de alternativas insuficiente";
    private final static String FILE_LOG_ERROS_Dados4 = "Numero de criterios insuficientes";
    /**
     * @param args the command line arguments
     */
    public static void main(/*String[] args*/String nomeFich, String output) throws FileNotFoundException {
        double[] pesos = null, vetorDistanciaIdeaIN, vetorDistanciaIdealP, vetorPrioridadeComposta;
        double[][] mCriterios, mNorm, mPesada, mValoresIdeais;
        String[][] totalInput = new String[100][50];
        //String nomeFich = "inputTOPSIS.txt", output = "outputTOPSIS.txt"; Testes
        String[] custos, criterios, alternativas, melhorOpcao, faltaCustos = {"crt_custo", "777"};
        int nLinhas = 0, nElementos = 0, a = 0;
        //nLinhas = LerFicheiroInput(nomeFich, totalInput, nLinhas);
         nLinhas = LerFicheiroInput(nomeFich, totalInput, nLinhas);
        if (nLinhas < 7) {
            gravarErros(FILE_LOG_ERROS_Dados3, "0");
        } else {
            if (!totalInput[0][0].equals("crt_beneficio") || !totalInput[1][0].equals("crt_custo")) {
                gravarErros(FILE_LOG_ERROS_Dados1, "1 ou 2");
            } else {
                nElementos = encontrarNEelementos(totalInput[1]);
                if (nElementos == 0) {
                    custos = comporArray(faltaCustos, nElementos + 1);
                } else {
                    custos = comporArray(totalInput[1], nElementos);
                }
                if (totalInput[2][0].equals("vec_pesos") && totalInput[3][0].contains(".")) {
                    nElementos = encontrarNEelementos(totalInput[2]);
                    pesos = gravarVetorPesos(totalInput[3], nElementos);
                } else if (totalInput[2][0].equals("md_crt_alt") && totalInput[3][0].equals("crt")) {
                    nElementos = encontrarNEelementos(totalInput[3]);
                    a = -2;
                    if (nElementos > 1) {
                        System.out.println("Vetor Pesos não introduzido");
                        pesos = criarVetorPesos(nElementos);
                    } else {
                        gravarErros(FILE_LOG_ERROS_Dados4, " 3 ");
                    }
                }

                if (totalInput[4 + a][0].equals("md_crt_alt")) {
                    if (totalInput[5 + a][0].equals("crt")) {
                        nElementos = encontrarNEelementos(totalInput[5 + a]);
                        criterios = comporArray(totalInput[5 + a], nElementos);
                        if (totalInput[6 + a][0].equals("alt")) {
                            nElementos = encontrarNEelementos(totalInput[6 + a]);
                            alternativas = comporArray(totalInput[6 + a], nElementos);
                            mCriterios = criarMatrizCriterios(totalInput, alternativas, criterios, a);
                            mNorm = matrizNormalizada(mCriterios, alternativas, criterios);
                            mPesada = matrizPesada(mNorm, pesos);
                            mValoresIdeais = selectSolucoes(mPesada, criterios, custos);
                            vetorDistanciaIdealP = detDistanciaIdealP(mPesada, alternativas, criterios, mValoresIdeais);
                            vetorDistanciaIdeaIN = detDistanciaIdealN(mPesada, alternativas, criterios, mValoresIdeais);
                            vetorPrioridadeComposta = vetorSolucao(vetorDistanciaIdeaIN, vetorDistanciaIdealP, alternativas);
                            melhorOpcao = melhorAlternativa(vetorPrioridadeComposta, alternativas);
                            printConsola(totalInput, nLinhas, mPesada, vetorPrioridadeComposta, melhorOpcao);
                            guardarOutputTotalTXT(output, criterios, alternativas, pesos, mCriterios, mNorm, mPesada, mValoresIdeais, vetorDistanciaIdealP, vetorDistanciaIdeaIN, vetorPrioridadeComposta, melhorOpcao);
                        } else {
                            gravarErros(FILE_LOG_ERROS_Dados1, Double.toString(6 + a));
                        }
                    } else {
                        gravarErros(FILE_LOG_ERROS_Dados1, Double.toString(5 + a));;
                    }
                } else {
                    gravarErros(FILE_LOG_ERROS_Dados2, "0");
                }

            }

        }

    }

    public static void gravarErros(String erro, String linha) throws FileNotFoundException {
        Formatter log = new Formatter(new File(FILE_LOG_ERROS));
        if (linha.equals("0")) {
            log.format(erro);
            System.out.println(erro);
        } else {
            log.format(erro + " na linha " + linha);
            System.out.println(erro + " na linha " + linha);
        }
        log.close();
    }

    /**
     *
     * @param Input
     * @param totalInput, dados retirados do ficheiro de input gravada linha a
     * linha
     * @param nLinhas, numero de linhas com informacao do ficheiro input
     * @return
     * @throws FileNotFoundException
     */
    public static int LerFicheiroInput(String Input, String[][] totalInput, int nLinhas) throws FileNotFoundException {
        Scanner readFile = new Scanner(new File(Input));
        while (readFile.hasNext()) {
            String linhaDados = readFile.nextLine();
            if (linhaDados.length() > 0) {
                nLinhas = gravarInput(linhaDados, nLinhas, totalInput);
            }
        }
        readFile.close();
        return nLinhas;
    }

    /**
     *
     * @param linhaDados, linha de dados extraida do input
     * @param nLinhas, numero total de linhas com informacao do ficheiro de
     * input
     * @param totalInput, matriz com os dados retirados do ficheiro de input
     * gravada linha a linha
     * @return
     */
    public static int gravarInput(String linhaDados, int nLinhas, String[][] totalInput) {
        String[] temp = linhaDados.split(" +");
        for (int i = 0; i < temp.length; i++) {
            totalInput[nLinhas][i] = temp[i];
        }
        nLinhas++;
        return nLinhas;
    }

    public static double[][] tratarDados(String[][] totalInput, int nLinhas, String[] beneficios, String[] custos, double[] pesos, String[] criterios, String[] alternativas) {
        double[][] mCriterios;
        int nElementos, a = 0;
        String[] faltaCustos = {"crt_custo", "777"};
        if (nLinhas < 7) {
            //logErros
        } else {
            if (!totalInput[0][0].equals("crt_beneficio") || !totalInput[1][0].equals("crt_custo")) {
                //logErros
            } else {
                nElementos = encontrarNEelementos(totalInput[0]);
                beneficios = comporArray(totalInput[0], nElementos);
                if (totalInput[1].length == 1) {
                    nElementos = 1;
                    custos = comporArray(faltaCustos, nElementos);
                } else {
                    nElementos = encontrarNEelementos(totalInput[1]);
                    custos = comporArray(totalInput[1], nElementos);
                }

                if (totalInput[2][0].equals("vec_pesos") && totalInput[3][0].contains(".")) {
                    nElementos = encontrarNEelementos(totalInput[2]);
                    pesos = gravarVetorPesos(totalInput[3], nElementos);
                } else if (totalInput[2][0].equals("md_alt_crt") && totalInput[3][0].equals("crt")) {
                    nElementos = encontrarNEelementos(totalInput[3]);
                    a = -2;
                    if (nElementos > 1) {
                        System.out.println("Vetor Pesos não introduzido");
                        pesos = criarVetorPesos(nElementos);
                    } else {
                        //logErros
                    }
                }

                if (totalInput[4 + a][0].equals("md_alt_crt")) {
                    if (totalInput[5 + a][0].equals("crt")) {
                        nElementos = encontrarNEelementos(totalInput[5 + a]);
                        criterios = comporArray(totalInput[5 + a], nElementos);
                        if (totalInput[6 + a][0].equals("alt")) {
                            nElementos = encontrarNEelementos(totalInput[6 + a]);
                            alternativas = comporArray(totalInput[6 + a], nElementos);
                        } else {
                            //logErros
                        }
                    } else {
                        //logErros    
                    }
                } else {
                    //logErros
                }

            }

        }
        return criarMatrizCriterios(totalInput, alternativas, criterios, a);
    }

    public static double[] criarVetorPesos(int nCriterios) {
        double[] pesos = new double[nCriterios];
        double peso = 0;
        peso = ((double) 1 / nCriterios);
        for (int i = 0; i < pesos.length; i++) {
            pesos[i] = peso;

        }
        return pesos;
    }

    /**
     *
     * @param linha, linha da matriz
     * @return numero de elementos da matriz - 1 porque o primeiro elemento é
     * descritivo do que esta na linha
     */
    public static int encontrarNEelementos(String[] linha) {
        int nElementos = 0;
        for (int i = 0; i < linha.length; i++) {
            if (linha[i] != null) {
                nElementos++;
            }
        }
        return nElementos - 1;
    }

    /**
     *
     * @param dados, valores que vêm do input que descrevem a importancia
     * relativa de cada criterio, a soma de todos os valores tem que ser igual a
     * 1
     * @param nElementos
     * @return sintetisa a informação da linha para dentro do array
     */
    public static String[] comporArray(String[] dados, int nElementos) {
        String[] temp = new String[nElementos];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = dados[i + 1];
        }
        return temp;
    }

    /**
     *
     * @param dados, valores que vêm do input que descrevem a importancia
     * relativa de cada criterio, a soma de todos os valores tem que ser igual a
     * 1
     * @param criterios, nomes dos critérios dispostos num array
     * @return array que possui o peso de cada critério de acordo com o critério
     * correspondente
     */
    public static double[] gravarVetorPesos(String[] dados, int nCriterios) {
        double[] pesos = new double[nCriterios];
        for (int i = 0; i < pesos.length; i++) {
            pesos[i] = Double.parseDouble(dados[i]);

        }
        return pesos;
    }

    /**
     *
     * @param totalnput, dados retirados do ficheiro de input gravada linha a
     * linha
     * @param alternativas, array que contem o nome de todas as opçoes
     * @param criterios, nomes dos critérios dispostos num array
     * @return matriz que contem os valores da classificação de cada alternativa
     * de acordo com cada critério
     */
    public static double[][] criarMatrizCriterios(String[][] totalInput, String[] alternativas, String[] criterios, int a) {
        double[][] mCriterios = new double[alternativas.length][criterios.length];
        for (int i = 0; i < alternativas.length; i++) {
            for (int j = 0; j < criterios.length; j++) {
                mCriterios[i][j] = Double.parseDouble(totalInput[i + 7 + a][j]);
            }
        }
        return mCriterios;
    }

    /**
     *
     * @param mCriterios, matriz que contem os valores da classificação de cada
     * alternativa de acordo com cada critério
     * @param alternativas, array que contem o nome de todas as opçoes
     * @param criterios, nomes dos critérios dispostos num array
     * @return resultado do metodo operaçãoSomatorio, matriz pesada, ou seja,
     * resultado da divisao de cada elemento de mCriterios pela raiz do
     * somatorio dos seus quadrados
     */
    public static double[][] matrizNormalizada(double[][] mCriterios, String[] alternativas, String[] criterios) {
        double temp[][] = new double[alternativas.length][criterios.length];
        for (int i = 0; i < alternativas.length; i++) {
            for (int j = 0; j < criterios.length; j++) {
                temp[i][j] = Math.pow(mCriterios[i][j], 2);
            }
        }

        return operaracaoSomatorio(temp, mCriterios);
    }

    /**
     *
     * @param temp, matriz temporária utilizada para guardar valores de
     * mCriterios elevados ao quadrado
     * @param mCriterios, matriz que contem os valores da classificação de cada
     * alternativa de acordo com cada critério
     * @return matriz pesada, ou seja, resultado da divisao de cada elemento de
     * mCriterios pela raiz do somatorio dos seus quadrados
     */
    public static double[][] operaracaoSomatorio(double[][] temp, double[][] mCriterios) {
        double soma;
        double[] somatorio = new double[temp[0].length];
        double[][] temp1 = new double[temp.length][temp[0].length];
        for (int i = 0; i < temp[0].length; i++) {
            soma = 0;
            for (int j = 0; j < temp.length; j++) {
                soma = soma + temp[j][i];
            }
            somatorio[i] = Math.sqrt(soma);
        }
        for (int i = 0; i < mCriterios.length; i++) {
            for (int j = 0; j < mCriterios[i].length; j++) {
                temp1[i][j] = mCriterios[i][j] / somatorio[j];
            }
        }
        return temp1;

    }

    /**
     *
     * @param mNorm, matriz pesada, ou seja, resultado da divisao de cada
     * elemento de mCriterios pela raiz do somatorio dos seus quadrados
     * @param pesos, array que contem o peso de cada criterio
     * @return matriz intermedia resultante da multiplicação de cada valor da
     * matriz pesada pelo critério da coluna a que pertemcem
     */
    public static double[][] matrizPesada(double[][] mNorm, double[] pesos) {
        double[][] temp = new double[mNorm.length][mNorm[0].length];
        for (int i = 0; i < mNorm.length; i++) {
            for (int j = 0; j < mNorm[i].length; j++) {
                temp[i][j] = mNorm[i][j] * pesos[j];
            }
        }
        return temp;
    }

    /**
     *
     * @param mPesada, matriz resultante da multiplicaçao de cada valor da
     * matriz pesada pelo critério da coluna a que pertencem
     * @param criterios, nomes dos critérios dispostos num array
     * @param custos, array com o nome dos criterios custo
     * @return matriz que contem os valores da solucao ideal e da solucao ideal
     * negativa
     */
    public static double[][] selectSolucoes(double[][] mPesada, String[] criterios, String[] custos) {
        double[][] mValoresIdeais = new double[2][criterios.length];
        double ideal, idealNEG;
        for (int i = 0; i < mPesada[0].length; i++) {
            ideal = 0;
            idealNEG = 100000;
            for (int j = 0; j < mPesada.length; j++) {

                if (ideal < mPesada[j][i]) {
                    mValoresIdeais[0][i] = mPesada[j][i];
                    ideal = mPesada[j][i];
                }
                if (idealNEG > mPesada[j][i]) {
                    mValoresIdeais[1][i] = mPesada[j][i];
                    idealNEG = mPesada[j][i];

                }
            }

        }
        return ordenarValoresIdeais(custos, criterios, mValoresIdeais);
    }

    /**
     *
     * @param custos, array com o nome dos criterios custo
     * @param criterios, nomes dos critérios dispostos num array
     * @param matrizSolucao, matriz vazia onde serão escritos os valores das
     * soluçoes ideais
     * @return matriz que contem os valores da solucao ideal e da solucao ideal
     * negativa
     */
    public static double[][] ordenarValoresIdeais(String[] custos, String[] criterios, double[][] mValoresIdeais) {
        double aux;
        for (int i = 0; i < custos.length; i++) {
            for (int j = 0; j < criterios.length; j++) {
                if (criterios[j].equals(custos[i])) {
                    aux = mValoresIdeais[0][j];
                    mValoresIdeais[0][j] = mValoresIdeais[1][j];
                    mValoresIdeais[1][j] = aux;
                }
            }
        }
        return mValoresIdeais;
    }

    /**
     *
     * @param mPesada, matriz resultante da multiplicaçao de cada valor da
     * matriz pesada pelo critério da coluna a que pertencem
     * @param alternativas, array que contem o nome de todas as opçoes
     * @param criterios, nomes dos critérios dispostos num array
     * @param matrizSolucao, matriz que contem os valores da solucao ideal e da
     * solucao ideal negativa
     * @return array que contem os valores da separação de cada alternativa até
     * a solução ideal
     */
    public static double[] detDistanciaIdealP(double[][] mPesada, String[] alternativas, String[] criterios, double[][] mValoresIdeais) {
        double[] vetorDistanciaIdealP = new double[alternativas.length];
        double soma, subtracao;
        for (int i = 0; i < vetorDistanciaIdealP.length; i++) {
            subtracao = 0;
            soma = 0;
            for (int j = 0; j < mPesada[0].length; j++) {
                subtracao = mPesada[i][j] - mValoresIdeais[0][j];
                soma = (Math.pow(subtracao, 2)) + soma;
            }
            vetorDistanciaIdealP[i] = Math.sqrt(soma);
        }
        return vetorDistanciaIdealP;
    }

    /**
     *
     * @param mPesada, matriz resultante da multiplicaçao de cada valor da
     * matriz pesada pelo critério da coluna a que pertencem
     * @param alternativas, array que contem o nome de todas as opçoes
     * @param criterios, nomes dos critérios dispostos num array
     * @param matrizSolucao matriz que contem os valores da solucao ideal e da
     * solucao ideal negativa
     * @return array que contem os valores da separação de cada alternativa até
     * a solução ideal negativa
     */
    public static double[] detDistanciaIdealN(double[][] mPesada, String[] alternativas, String[] criterios, double[][] mValoresIdeais) {
        double[] vetorDistanciaIdeaIN = new double[alternativas.length];
        double soma, subtracao;
        for (int i = 0; i < vetorDistanciaIdeaIN.length; i++) {
            soma = 0;
            subtracao = 0;
            for (int j = 0; j < mPesada[0].length; j++) {
                subtracao = mPesada[i][j] - mValoresIdeais[1][j];
                soma = (Math.pow(subtracao, 2)) + soma;
            }
            vetorDistanciaIdeaIN[i] = Math.sqrt(soma);
        }
        return vetorDistanciaIdeaIN;
    }

    /**
     *
     * @param matrizSeparacaoIdealN, array que contem os valores da diferença de
     * cada alternativa até a solução ideal negativa
     * @param matrizSeparacaoIdealP, array que contem os valores da diferença de
     * cada alternativa até a solução ideal
     * @param alternativas, array que contem o nome de todas as opçoes
     * @return, array com os valores resultantes da operação Ci*=Si'/(Si'+Si*)
     * do final do metodo topsis para cada alternativa
     */
    public static double[] vetorSolucao(double[] vetorDistanciaIdeaIN, double[] vetorDistanciaIdealP, String[] alternativas) {
        double valor = 0;
        double[] vetorPrio = new double[alternativas.length];
        for (int i = 0; i < alternativas.length; i++) {
            valor = vetorDistanciaIdeaIN[i] / (vetorDistanciaIdeaIN[i] + vetorDistanciaIdealP[i]);
            vetorPrio[i] = valor;
        }
        return vetorPrio;
    }

    /**
     *
     * @param vetorPrioridadeComposta, array com os valores resultantes da
     * operação Ci*=Si'/(Si'+Si*) do final do metodo topsis para cada
     * alternativa
     * @param alternativas, array que contem o nome de todas as opçoes
     * @return array em que se contem o nome da melhor alternativa e o valor da
     * prioridade da mesma
     */
    public static String[] melhorAlternativa(double[] vetorPrioridadeComposta, String[] alternativas) {
        double valorTemp = 0;
        int indiceTemp = 0;
        String[] resposta = new String[2];
        for (int i = 0; i < alternativas.length; i++) {
            if (vetorPrioridadeComposta[i] > valorTemp) {
                resposta[1] = Double.toString(vetorPrioridadeComposta[i]);
                indiceTemp = i;
                valorTemp = vetorPrioridadeComposta[i];
            }
        }
        resposta[0] = alternativas[indiceTemp];
        return resposta;
    }

    public static void printMatrizString(String[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%25s", matriz[i][j]);
            }
            System.out.println("");
        }
    }

    public static void printMatrizDouble(double[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%25s", String.valueOf(matriz[i][j]));
            }
            System.out.println("");
        }
    }

    private static void printMatrizStringInput(String[][] matriz, int nLinhas) {
        for (int i = 0; i < nLinhas + 3; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%18s", matriz[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }

    public static void printConsola(String[][] totalInput, int nLinhas, double[][] mPesada, double[] vetorPrioridadeComposta, String[] melhorOpcao) {
        printMatrizStringInput(MetodoAHP.eliminarNull(totalInput), nLinhas);
        System.out.println(" ");
        System.out.println("Matriz Normalizada Pesada");
        printMatrizDouble(mPesada);
        System.out.println("Vetor Proximidade Relativa");
        for (int i = 0; i < vetorPrioridadeComposta.length; i++) {
            System.out.printf("%25s", vetorPrioridadeComposta[i]);
        }
        System.out.println(" ");
        System.out.println("Melhor Alternativa");
        System.out.println("Nome: " + melhorOpcao[0]);
        System.out.println("Pontuação: " + melhorOpcao[1]);
    }

    public static void guardarOutputTotalTXT(String output, String[] criterios, String[] alternativas, double[] pesos, double[][] mCriterios, double[][] mNorm, double[][] mPesada, double[][] mValoresIdeais, double[] matrizSeparacaoIdealP, double[] matrizSeparacaoIdealN, double[] vetorPrioridadeComposta, String[] melhorOpcao) throws FileNotFoundException {
        Formatter out = new Formatter(new File(output));
        printVectorTxt(out, "Vetor Pesos", criterios, pesos);
        out.format("%n");
        printMatrizTxt(out, mCriterios, alternativas, criterios, "Matriz Criterios");
        printMatrizTxt(out, mNorm, alternativas, criterios, "Matriz Normalizada");
        printMatrizTxt(out, mPesada, alternativas, criterios, "Matriz Normalizada Pesada");
        printVectorTxt(out, "Solucoes Ideais", criterios, mValoresIdeais[0]);
        printVectorTxt(out, "Solucoes Ideais Negativas", criterios, mValoresIdeais[1]);
        printVectorTxt(out, "Vetor Distancia a Solução Ideal", alternativas, matrizSeparacaoIdealP);
        printVectorTxt(out, "Vetor Distancia a Solução Ideal Negativa", alternativas, matrizSeparacaoIdealN);
        printVectorTxt(out, "Vetor Prioridade Relativa // Pontuação Final", alternativas, vetorPrioridadeComposta);
        out.format("%n");
        out.format("Melhor Alternativa");
        out.format("%n");
        out.format("Nome: " + melhorOpcao[0]);
        out.format("%n");
        out.format("Pontuação: " + melhorOpcao[1]);
        out.close();
    }

    public static void printVectorTxt(Formatter out, String titulo, String[] cabecalho, double[] vetor) {
        out.format("%n");
        out.format("%25s", titulo);
        out.format("%n");
        for (int a = 0; a < cabecalho.length; a++) {
            out.format("%25s", "   " + cabecalho[a]);
        }
        out.format("%n");
        for (int a = 0; a < vetor.length; a++) {
            out.format("%25s", "   " + vetor[a]);
        }
        out.format("%n");
    }

    public static void printMatrizTxt(Formatter out, double[][] matriz, String[] alt, String[] crt, String mNome) {
        String[][] mOutput = new String[matriz.length + 1][matriz[0].length + 1];
        mOutput[0][0] = mNome;
        for (int i = 0; i < mOutput[0].length - 1; i++) {
            mOutput[0][i + 1] = crt[i];
        }
        for (int i = 0; i < mOutput.length - 1; i++) {
            mOutput[i + 1][0] = alt[i];
        }
        for (int i = 0; i < mOutput.length - 1; i++) {
            for (int j = 0; j < mOutput[0].length - 1; j++) {
                mOutput[i + 1][j + 1] = String.valueOf(matriz[i][j]);
            }

        }
        for (int i = 0; i < mOutput.length; i++) {
            for (int j = 0; j < mOutput[0].length; j++) {
                out.format("%25s", mOutput[i][j]);
            }
            out.format("%n");
        }
        out.format("%n");
    }
}
