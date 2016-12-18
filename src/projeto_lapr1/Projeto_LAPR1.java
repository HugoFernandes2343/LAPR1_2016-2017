/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projeto_lapr1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.la4j.Matrix;
import org.la4j.matrix.DenseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.decomposition.EigenDecompositor;

/**
 *
 * @author PC
 */
public class Projeto_LAPR1 {
    public static Scanner sc = new Scanner(System.in);
    public static double[] RandomConsistency = {0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.42};/*Vetor Randômio até ao n=8*/
    public static String[] v_criterios = new String[4];
    public static String[] v_criterio1 = new String[5];
    public static String[] v_criterio2 = new String[5];
    public static String[] v_criterio3 = new String[5];
    public static double[][] mc_criterios = new double[3][3];
    public static double[][] matrizCriterio1 = new double[4][4];
    public static double[][] matrizCriterio2 = new double[4][4];
    public static double[][] matrizCriterio3 = new double[4][4];
    public static double[][] matrizSomatorios=new double[4][4];

    public static void main(String[] args) throws FileNotFoundException {
        int nLinhas,op;
        do {
            op = menu();
            switch (op) {
                case 1:
                    nLinhas=0;
                    nLinhas = LerFicheiroInput(nLinhas, v_criterios, mc_criterios, v_criterio1, matrizCriterio1, v_criterio2, matrizCriterio2, v_criterio3, matrizCriterio3);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    matrizSomatorios=criarMatrizSomatorios(matrizSomatorios,mc_criterios,matrizCriterio1,matrizCriterio2,matrizCriterio3);
                    break;
                case 2:
                    nLinhas=0;
                    nLinhas = LerFicheiroInput(nLinhas, v_criterios, mc_criterios, v_criterio1, matrizCriterio1, v_criterio2, matrizCriterio2, v_criterio3, matrizCriterio3);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    criarMatrizSomatorios(matrizSomatorios,mc_criterios,matrizCriterio1,matrizCriterio2,matrizCriterio3);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção incorreta.Repita");
                    break;
            }
        } while (op != 0);
    }
    
    private static int menu() {
        String texto = "\nMenu:"
                + "\n Efetuar a operação com valores aproximados (Digite 1)"
                + "\n Efetuar a operação com valores exatos (Digite 2)"
                + "\n FIM (0)"
                + "\n Qual é a opção?";
        System.out.printf("%n%s%n", texto);
        int op = sc.nextInt();
        sc.nextLine();
        return op;
    }
    
    public static int LerFicheiroInput(int nLinhas, String[] v_criterios, double[][] mc_criterios, String[] v_criterio1, double[][] matrizCriterio1, String[] v_criterio2, double[][] matrizCriterio2, String[] v_criterio3, double[][] matrizCriterio3) throws FileNotFoundException {
        /*Formatter ler = new Formatter(new File("Dados.txt"));*/
        Scanner readFile = new Scanner(new File("Dados.txt"));
        while (readFile.hasNext()) {
            String linhaDados = readFile.nextLine();
            if (linhaDados.length() > 0) {
                nLinhas = tratarInput(linhaDados, v_criterios, mc_criterios, v_criterio1, matrizCriterio1, v_criterio2, matrizCriterio2, v_criterio3, matrizCriterio3, nLinhas);
            }
        }
        readFile.close();
        return nLinhas;
    }
    
    public static int tratarInput(String linhaDados, String[] v_criterios, double[][] mc_criterios, String[] v_criterio1, double[][] matrizCriterio1, String[] v_criterio2, double[][] matrizCriterio2, String[] v_criterio3, double[][] matrizCriterio3, int nLinhas) {
        String temp[] = linhaDados.split(" ");/*Split testes*/
        for (int j = 0; j < temp.length; j++) {
            if (nLinhas == 0) {
                v_criterios[j] = temp[j];
            } else if (nLinhas > 0 && nLinhas < 4) {
                mc_criterios[nLinhas - 1][j] = StringToDouble(temp, j);
            } else if (nLinhas == 4) {
                v_criterio1[j] = temp[j];
            } else if (nLinhas > 4 && nLinhas < 9) {
                matrizCriterio1[nLinhas - mc_criterios.length - 2/*Linhas ocupadas pelos vetores*/][j] = StringToDouble(temp, j);
            } else if (nLinhas == 9) {
                v_criterio2[j] = temp[j];
            } else if (nLinhas > 9 && nLinhas < 14) {
                matrizCriterio2[nLinhas - mc_criterios.length - matrizCriterio1.length - 3][j] = StringToDouble(temp, j);
            } else if (nLinhas == 14) {
                v_criterio3[j] = temp[j];
            } else if (nLinhas > 14) {
                matrizCriterio3[nLinhas - mc_criterios.length - matrizCriterio1.length - matrizCriterio3.length - 4][j] = StringToDouble(temp, j);
            }
        }
        nLinhas++;
        return nLinhas;
    }
    
    public static double StringToDouble(String[] temp, int j) {
        if (temp[j].contains("/")) {
            String[] tempDiv = temp[j].split("/");
            return Double.parseDouble(tempDiv[0]) / Double.parseDouble(tempDiv[1]);
        } else {
            return Double.parseDouble(temp[j]);
        }
    }

    /*CASE 1*/
    public static double[][] criarMatrizSomatorios(double[][] matrizSomatorios,double[][] mc_criterios,double[][] matrizCriterio1,double[][] matrizCriterio2,double[][] matrizCriterio3){
            matrizSomatorios[0]=somatoriosColunas(mc_criterios);
            matrizSomatorios[1]=somatoriosColunas(matrizCriterio1);
            matrizSomatorios[2]=somatoriosColunas(matrizCriterio2);
            matrizSomatorios[3]=somatoriosColunas(matrizCriterio3);
        return matrizSomatorios;
    }
    
    public static double[] somatoriosColunas(double[][] matriz) {
        double[] somatorio = new double[matriz.length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                somatorio[i] = somatorio[i] + matriz[j][i];
            }
        }
        return somatorio;
    }
    
}
