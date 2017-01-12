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
        String[][] totalInput = new String[50][50];
        String nomeFich = "inputTOPSIS.txt";
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
        matrizSeparacaoIdealP = detSeparacaoP(mPesada, criterios, alternativas, matrizSolucao);
        matrizSeparacaoIdealN = detSeparacaoN(mPesada, criterios, alternativas, matrizSolucao);
        vetorPrioridadeComposta = vetorSolucao(matrizSeparacaoIdealP, matrizSeparacaoIdealN, alternativas);
        melhorOpcao = melhorAlternativa(vetorPrioridadeComposta, alternativas);
    }

    /**
     *
     * @param Input
     * @param totalInput
     * @param nLinhas
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
     * @param linhaDados
     * @param nLinhas
     * @param totalInput
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
     * @param linha
     * @return
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
     * @param dados
     * @param nElementos
     * @return
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
     * @param dados
     * @param criterios
     * @return
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
     * @param totalnput
     * @param alternativas
     * @param criterios
     * @return
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
     * @param mCriterios
     * @param alternativas
     * @param criterios
     * @return
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
     * @param temp
     * @param mCriterios
     * @return
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
     * @param mNorm
     * @param pesos
     * @return
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
     * @param mPesada
     * @param criterios
     * @param custos
     * @return
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
     * @param custos
     * @param criterios
     * @param matrizSolucao
     * @return
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
     * @param mPesada
     * @param alternativas
     * @param criterios
     * @param matrizSolucao
     * @return
     */
    public static double[] detSeparacaoP(double[][] mPesada, String[] alternativas, String[] criterios, double[][] matrizSolucao) {
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
     * @param mPesada
     * @param alternativas
     * @param criterios
     * @param matrizSolucao
     * @return
     */
    public static double[] detSeparacaoN(double[][] mPesada, String[] alternativas, String[] criterios, double[][] matrizSolucao) {
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
     * @param matrizSeparacaoIdealN
     * @param matrizSeparacaoIdealP
     * @return
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
     * @param vetorPrioridadeComposta array com os valores resultantes da operação Ci*=Si'/(Si'+Si*) do final do metodo topsis para cada alternativa
     * @param alternativas array que contem o nome de todas as opçoes
     * @return array em que se contem o nome da melhor alternativa e o valor da prioridade da mesma
     */
    public static String[] melhorAlternativa(double[] vetorPrioridadeComposta, String[] alternativas) {
        double valorTemp = 0;
        int indiceTemp = 0;
        String[] resposta = new String[2];
        for (int i = 0; i < alternativas.length; i++){
            if(vetorPrioridadeComposta[i]>valorTemp){
            resposta[1]= Double.toString(vetorPrioridadeComposta[i]);
            indiceTemp = i;
            valorTemp = vetorPrioridadeComposta[i];
            }
        }
        resposta[0] = alternativas[indiceTemp];
        return resposta;
    }
}
