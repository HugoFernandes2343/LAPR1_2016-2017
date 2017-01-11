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
        double[] pesos;
        double[][] MC;
        double[][] MNorm;
        String[][] totalInput = new String[50][50];
        String nomeFich = "inputTOPSIS.txt";
        String[] beneficios;
        String[] custos;
        String[] criterios;
        String[] alternativas;
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
        MC = criarMatrizCriterios(totalInput, alternativas, criterios);
        MNorm = matrizNormalizada(MC, alternativas, criterios);
    }

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

    public static int gravarInput(String linhaDados, int nLinhas, String[][] totalInput) {
        String[] temp = linhaDados.split(" +");
        for (int i = 0; i < temp.length; i++) {
            totalInput[nLinhas][i] = temp[i];
        }
        nLinhas++;
        return nLinhas;
    }

    public static int encontrarNEelementos(String[] linha) {
        int nElementos = 0;
        for (int i = 0; i < linha.length; i++) {
            if (linha[i] != null) {
                nElementos++;
            }
        }
        return nElementos - 1;
    }

    public static String[] comporArray(String[] dados, int nElementos) {
        String[] temp = new String[nElementos];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = dados[i + 1];
        }
        return temp;
    }

    public static double[] criarVetorPesos(String[] dados, String[] criterios) {
        double[] pesos = new double[criterios.length];
        for (int i = 0; i < pesos.length; i++) {
            pesos[i] = Double.parseDouble(dados[i]);

        }
        return pesos;
    }

    public static double[][] criarMatrizCriterios(String[][] totalnput, String[] alternativas, String[] criterios) {
        double[][] MC = new double[criterios.length][alternativas.length];
        for (int i = 0; i < MC.length; i++) {
            for (int j = 0; j < MC[i].length; j++) {
                MC[i][j] = Double.parseDouble(totalnput[i + 7][j]);
            }
        }
        return MC;
    }

    public static double[][] matrizNormalizada(double[][] MC, String[] alternativas, String[] criterios) {
        double temp[][] = new double[criterios.length][alternativas.length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[1].length; j++) {
                temp[i][j] = Math.pow(MC[i][j], 2);
            }
        }

        return operaracaoSomatorio(temp);
    }

    public static double[][] operaracaoSomatorio(double[][] temp) {
        double soma = 0;
        double[] somatorio = new double[temp[1].length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[1].length; j++) {
                soma = soma + temp[i][j];
            }
            somatorio[i] = soma;
        }
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[1].length; j++) {
                temp[i][j]=temp[i][j]/somatorio[i];
            }
        }
        return temp;
    }
}
