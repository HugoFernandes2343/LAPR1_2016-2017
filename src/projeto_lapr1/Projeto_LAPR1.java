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

    public static double[] RandomConsistency = {0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.42};/*Vetor Randômio até ao n=8*/
    public static int N_MATRIZES = 4;/*Nº de critérios + a matriz de comparação de critérios*/

    public static void main(String[] args) throws FileNotFoundException {
        int nLinhas = 0;
        String[] v_criterios=new String [4];String[] v_criterio1=new String [5];
        String[] v_criterio2=new String [5];String[] v_criterio3=new String [5];
        double[][] mc_criterios = new double[3][3];double[][] matrizCriterio1 = new double[4][4];
        double[][] matrizCriterio2 = new double[4][4];double[][] matrizCriterio3 = new double[4][4];
        nLinhas = LerFicheiroInput(nLinhas,v_criterios,mc_criterios,v_criterio1,matrizCriterio1,v_criterio2,matrizCriterio2,v_criterio3,matrizCriterio3);
        
        Matrix criterios = new Basic2DMatrix(mc_criterios);
        Matrix criterio1 = new Basic2DMatrix(matrizCriterio1);
        Matrix criterio2 = new Basic2DMatrix(matrizCriterio2);
        Matrix criterio3 = new Basic2DMatrix(matrizCriterio3);
        
        FazerVetoresProprios(criterios,criterio1,criterio2,criterio3);
    }

    public static int LerFicheiroInput(int nLinhas,String[] v_criterios,double[][] mc_criterios,String[] v_criterio1,double[][] matrizCriterio1,String[] v_criterio2,double[][] matrizCriterio2,String[] v_criterio3,double[][] matrizCriterio3) throws FileNotFoundException {

        /*Formatter ler = new Formatter(new File("Dados.txt"));*/
        Scanner readFile = new Scanner(new File("Dados.txt"));
        while (readFile.hasNext()) {
            String linhaDados = readFile.nextLine();
            if (linhaDados.length() > 0) {
                nLinhas = tratarInput(linhaDados,v_criterios,mc_criterios,v_criterio1, matrizCriterio1,v_criterio2, matrizCriterio2,v_criterio3, matrizCriterio3, nLinhas);
            }
        }
        readFile.close();
        return nLinhas;
    }

    public static int tratarInput(String linhaDados, String[] v_criterios,double[][] mc_criterios,String[] v_criterio1, double[][] matrizCriterio1,String[] v_criterio2, double[][] matrizCriterio2,String[] v_criterio3, double[][] matrizCriterio3, int nLinhas) {
        String temp[] = linhaDados.split(" ");/*Split testes*/
        /*String nomeMatriz = temp[0];*/
        for (int j = 0; j < temp.length; j++) {
            if (temp.length == 4 && nLinhas == 0) {
                v_criterios[j] = temp[j];
            } else if (temp.length == 3 && nLinhas > 0 && nLinhas < 5) {
                mc_criterios[nLinhas-1][j] = StringToDouble(temp,j);
            } else if (temp.length == 5 && nLinhas == 4) {
                v_criterio1[j] = temp[j];
            } else if (temp.length == 4 && nLinhas > 4 && nLinhas < 9) {
                matrizCriterio1[nLinhas - mc_criterios.length-2/*Linhas ocupadas pelos vetores*/][j] = StringToDouble(temp,j);
            } else if (temp.length == 5 && nLinhas == 9) {
                v_criterio2[j] = temp[j];
            } else if (temp.length == 4 && nLinhas > 9 && nLinhas < 14) {
                matrizCriterio2[nLinhas - mc_criterios.length - matrizCriterio1.length-3][j] = StringToDouble(temp,j);
            } else if (temp.length == 5 && nLinhas == 14) {
                v_criterio3[j] = temp[j];
            } else if (temp.length == 4 && nLinhas > 14) {
                matrizCriterio3[nLinhas - mc_criterios.length - matrizCriterio1.length - matrizCriterio3.length-4][j] = StringToDouble(temp,j);
            }
        }
        nLinhas++;
        return nLinhas;
    }
    
    public static Double StringToDouble(String[] temp,int j){
        if(temp[j].contains("/")){
            String[] tempDiv = temp[j].split("/");
            return Double.parseDouble(tempDiv[0])/Double.parseDouble(tempDiv[1]);
        }else{
            return Double.parseDouble(temp[j]);
        }
    }
    
    public static void FazerVetoresProprios(Matrix criterios,Matrix criterio1,Matrix criterio2,Matrix criterio3){
        //pedir vetores proprios e valores
        EigenDecompositor eigenD = new EigenDecompositor(criterios);
        Matrix[] criteriosD=eigenD.decompose();
    }
}
