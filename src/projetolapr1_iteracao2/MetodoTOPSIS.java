package projetolapr1_iteracao2;

import java.io.File;
import java.util.Scanner;
import java.util.Formatter;
import java.io.FileNotFoundException;
import java.lang.Math;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class MetodoTOPSIS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        double[] pesos, matrizSeparacaoIdealN, matrizSeparacaoIdealP, vetorPrioridadeComposta;
        double[][] mCriterios, mNorm, mPesada, matrizSolucao;
        String[][] totalInput = new String[100][50];
        String nomeFich = "inputTOPSIS.txt", output = "outputTOPSIS.txt";
        String[] beneficios, custos, criterios, alternativas, melhorOpcao;
        int nLinhas = 0, nElementos = 0;

        nLinhas = LerFicheiroInput(nomeFich, totalInput, nLinhas);
        nElementos = encontrarNEelementos(totalInput[0]);
        beneficios = comporArray(totalInput[0], nElementos);
        nElementos = encontrarNEelementos(totalInput[1]);
        custos = comporArray(totalInput[1], nElementos);
        nElementos = encontrarNEelementos(totalInput[2]);
        criterios = comporArray(totalInput[2], nElementos);
        pesos = criarVetorPesos(totalInput[3], criterios);
        nElementos = encontrarNEelementos(totalInput[6]);
        alternativas = comporArray(totalInput[6], nElementos);
        mCriterios = criarMatrizCriterios(totalInput, alternativas, criterios);
        mNorm = matrizNormalizada(mCriterios, alternativas, criterios);
        mPesada = matrizPesada(mNorm, pesos);
        matrizSolucao = selectSolucoes(mPesada, criterios, custos);
        matrizSeparacaoIdealP = detSeparacaoIdealP(mPesada, criterios, alternativas, matrizSolucao);
        matrizSeparacaoIdealN = detSeparacaoIdealN(mPesada, criterios, alternativas, matrizSolucao);
        vetorPrioridadeComposta = vetorSolucao(matrizSeparacaoIdealP, matrizSeparacaoIdealN, alternativas);
        melhorOpcao = melhorAlternativa(vetorPrioridadeComposta, alternativas);
        selecaoOutput(output, beneficios, custos, criterios, totalInput, alternativas, pesos, mCriterios, mNorm, mPesada, matrizSolucao, matrizSeparacaoIdealP, matrizSeparacaoIdealN, vetorPrioridadeComposta, melhorOpcao, nLinhas);
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
    public static double[] criarVetorPesos(String[] dados, String[] criterios) {
        double[] pesos = new double[criterios.length];
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
    public static double[][] criarMatrizCriterios(String[][] totalnput, String[] alternativas, String[] criterios) {
        double[][] mCriterios = new double[criterios.length][alternativas.length];
        for (int i = 0; i < mCriterios.length; i++) {
            for (int j = 0; j < mCriterios[i].length; j++) {
                mCriterios[i][j] = Double.parseDouble(totalnput[i + 7][j]);
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
        double temp[][] = new double[criterios.length][alternativas.length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[1].length; j++) {
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
        for (int i = 0; i < temp.length; i++) {
            soma = 0;
            for (int j = 0; j < temp[i].length; j++) {
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
        double[][] matrizSolucao = new double[2][criterios.length];
        double ideal, idealNEG;
        for (int i = 0; i < mPesada.length; i++) {
            ideal = 0;
            idealNEG = 100000;
            for (int j = 0; j < mPesada.length; j++) {

                if (ideal < mPesada[j][i]) {
                    matrizSolucao[0][i] = mPesada[j][i];
                    ideal = mPesada[j][i];
                }
                if (idealNEG > mPesada[j][i]) {
                    matrizSolucao[1][i] = mPesada[j][i];
                    idealNEG = mPesada[j][i];

                }
            }

        }
        return ordenarValoresIdeais(custos, criterios, matrizSolucao);
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
    public static double[][] ordenarValoresIdeais(String[] custos, String[] criterios, double[][] matrizSolucao) {
        double aux;
        for (int i = 0; i < custos.length; i++) {
            for (int j = 0; j < criterios.length; j++) {
                if (criterios[j].equals(custos[i])) {
                    aux = matrizSolucao[0][j];
                    matrizSolucao[0][j] = matrizSolucao[1][j];
                    matrizSolucao[1][j] = aux;
                }
            }
        }
        return matrizSolucao;
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
    public static double[] detSeparacaoIdealP(double[][] mPesada, String[] alternativas, String[] criterios, double[][] matrizSolucao) {
        double[] matrizSeparacaoIdealP = new double[alternativas.length];
        double soma, subtracao;
        for (int i = 0; i < matrizSeparacaoIdealP.length; i++) {
            subtracao = 0;
            soma = 0;
            for (int j = 0; j < mPesada.length; j++) {
                subtracao = mPesada[i][j] - matrizSolucao[0][j];
                soma = (Math.pow(subtracao, 2)) + soma;
            }
            matrizSeparacaoIdealP[i] = Math.sqrt(soma);
        }
        return matrizSeparacaoIdealP;
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
    public static double[] detSeparacaoIdealN(double[][] mPesada, String[] alternativas, String[] criterios, double[][] matrizSolucao) {
        double[] matrizSeparacaoIdealN = new double[alternativas.length];
        double soma, subtracao;
        for (int i = 0; i < matrizSeparacaoIdealN.length; i++) {
            soma = 0;
            subtracao = 0;
            for (int j = 0; j < mPesada.length; j++) {
                subtracao = mPesada[i][j] - matrizSolucao[1][j];
                soma = (Math.pow(subtracao, 2)) + soma;
            }
            matrizSeparacaoIdealN[i] = Math.sqrt(soma);
        }
        return matrizSeparacaoIdealN;
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
    public static double[] vetorSolucao(double[] matrizSeparacaoIdealN, double[] matrizSeparacaoIdealP, String[] alternativas) {
        double valor = 0;
        double[] vetorPrio = new double[alternativas.length];
        for (int i = 0; i < alternativas.length; i++) {
            valor = matrizSeparacaoIdealN[i] / (matrizSeparacaoIdealP[i] + matrizSeparacaoIdealN[i]);
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
                System.out.printf("%25s", matriz[i][j]);
            }
            System.out.println("");
        }
    }

    public static void selecaoOutput(String output, String[] beneficios, String[] custos, String[] criterios, String[][] totalInput, String[] alternativas, double[] pesos, double[][] mCriterios, double[][] mNorm, double[][] mPesada, double[][] matrizSolucao, double[] matrizSeparacaoIdealP, double[] matrizSeparacaoIdealN, double[] vetorPrioridadeComposta, String[] melhorOpcao, int nLinhas) {
        int nLinhasOutput = 0;
        String[][] matrizTotalOutput=new String[][];
        printConsola(totalInput, nLinhas, mPesada, vetorPrioridadeComposta, melhorOpcao);
        nLinhasOutput=juntarDados(matrizTotal)
    }

    public static int juntarDados(String[][] matrizTotal, int nLinhasOutput, int op, double[][] matrizCriterios, String[][] m_cabecalhos, double[][] matrizTotalCriterios, double[][] matrizSomatorios, double[][] matrizCriteriosNormalizada, double[][] matrizTotalNormalizacao, double[][] mPrioridadeRelativa) {
        int nColuna = 0;
        for (int nMatriz = 0; nMatriz < N_CRITERIOS + 1; nMatriz++) {
            if (nMatriz == 0) {
                nLinhasOutput = adicionarDadosCabecalho(matrizTotal, m_cabecalhos[nMatriz], nLinhasOutput);
                nLinhasOutput = adicionarDadosMatrizes(matrizTotal, DoubleToString(matrizCriterios, op), nLinhasOutput);
                nLinhasOutput++;
            } else {
                nLinhasOutput = adicionarDadosCabecalho(matrizTotal, m_cabecalhos[nMatriz], nLinhasOutput);
                nLinhasOutput = adicionarDadosMatrizes(matrizTotal, DoubleToString(identificarMatriz(matrizTotalCriterios, nMatriz - 1, N_ALTERNATIVAS), op), nLinhasOutput);
                nLinhasOutput++;
            }
        }
        for (int nMatriz = 0; nMatriz < N_CRITERIOS + 1; nMatriz++) {
            if (nMatriz == 0) {
                nLinhasOutput = adicionarDadosCabecalho(matrizTotal, m_cabecalhos[nMatriz], nLinhasOutput);
                nLinhasOutput = adicionarDadosMatrizes(matrizTotal, DoubleToString(matrizCriteriosNormalizada, op), nLinhasOutput);
                nColuna = adicionarVetorPrioridades(matrizTotal, DoubleToString(mPrioridadeRelativa, op), nLinhasOutput, nColuna);
                nLinhasOutput++;
            } else {
                nLinhasOutput = adicionarDadosCabecalho(matrizTotal, m_cabecalhos[nMatriz], nLinhasOutput);
                nLinhasOutput = adicionarDadosMatrizes(matrizTotal, DoubleToString(identificarMatriz(matrizTotalNormalizacao, nMatriz - 1, N_ALTERNATIVAS), op), nLinhasOutput);
                nColuna = adicionarVetorPrioridades(matrizTotal, DoubleToString(mPrioridadeRelativa, op), nLinhasOutput, nColuna);
                nLinhasOutput++;
            }
        }
        return nLinhasOutput;
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

    public static void guardarOutputTotalTXT(String output, String[] beneficios, String[] custos, String[] criterios, String[][] totalInput, String[] alternativas, double[] pesos, double[][] mCriterios, double[][] mNorm, double[][] mPesada, double[][] matrizSolucao, double[] matrizSeparacaoIdealP, double[] matrizSeparacaoIdealN, double[] vetorPrioridadeComposta, String[] melhorOpcao, int nLinhas) throws FileNotFoundException {
        Formatter out = new Formatter(new File(output));
        for (int i = 0; i < nLinhasOutput; i++) {
            for (int j = 0; j < matrizTotal[i].length; j++) {
                out.format("%20s", matrizTotal[i][j]);
            }
            out.format("%n");
        }
        if (confirmacaoDadosIrrelevantes(posicaoDadosIrrelevantes) == true) {
            out.format("%25s", "Atributos Ignorados:");
            out.format("%n");
            for (int i = 0; i < posicaoDadosIrrelevantes.length; i++) {
                if (posicaoDadosIrrelevantes[i] != null) {
                    out.format("%25s", posicaoDadosIrrelevantes[i]);
                }
            }
        }
        out.format("%n");
        /*out.format("%25s", "RC");
        out.format("%25s", "Valor Próprio Máx");
        out.format("%25s", "IR");
        out.format("%n");
        for (int a = 0; a < RCValues.length; a++) {
            for (int b = 0; b < RCValues[a].length; b++) {
                out.format("%25s", (a + 1) + " : " + (double) Math.round(RCValues[a][b] * 1000) / 1000);
            }
            out.format("%n");
        }*/
        out.format("%25s", "Vetor Distancia a Solução Ideal");
        out.format("%n");
        for (int a = 0; a < alternativas.length; a++) {
            out.format("%25s", "   " + alternativas[a]);
        }
        out.format("%n");
        for (int a = 0; a < matrizSeparacaoIdealP.length; a++) {
            out.format("%25s", "   " + matrizSeparacaoIdealP[a]);
        }
        out.format("%25s", "Vetor Distancia a Solução Ideal");
        out.format("%n");
        for (int a = 0; a < alternativas.length; a++) {
            out.format("%25s", "   " + alternativas[a]);
        }
        out.format("%n");
        for (int a = 0; a < matrizSeparacaoIdealP.length; a++) {
            out.format("%25s", "   " + matrizSeparacaoIdealP[a]);
        }
        printVectorTxt(out, "Solucoes Ideais", criterios, matrizSolucao[0]);
        out.format("%n");
        printVectorTxt(out, "Solucoes Ideais Negativos", criterios, matrizSolucao[1]);
        out.format("%n");
        printVectorTxt(out, "Vetor Distancia a Solução Ideal", alternativas, matrizSeparacaoIdealP);
        out.format("%n");
        printVectorTxt(out, "Vetor Distancia a Solução Ideal Negativa", alternativas, matrizSeparacaoIdealN);
        out.format("%n");
        printVectorTxt(out, "Vetor Prioridade Relativa // Pontuação Final", alternativas, vetorPrioridadeComposta);
        out.format("%n");
        out.format("Melhor Alternativa");
        out.format("Nome: " + melhorOpcao[0]);
        out.format("Pontuação: " + melhorOpcao[1]);
        out.close();
    }

    public static void printVectorTxt(Formatter out, String titulo, String[] cabecalho, double[] vetor) {
        out.format("%25s", titulo);
        out.format("%n");
        for (int a = 0; a < cabecalho.length; a++) {
            out.format("%25s", "   " + cabecalho[a]);
        }
        out.format("%n");
        for (int a = 0; a < vetor.length; a++) {
            out.format("%25s", " | " + vetor[a]);
        }
    }
}
