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
    public static final int N_MATRIZES = 4;
    public static final int N_CRITERIOS = 3;

    public static String[] v_criterios = new String[4];
    public static String[] v_criterio1 = new String[5];
    public static String[] v_criterio2 = new String[5];
    public static String[] v_criterio3 = new String[5];

    public static double[][] mc_criterios = new double[3][3];
    public static double[][] matrizCriterio1 = new double[4][4];
    public static double[][] matrizCriterio2 = new double[4][4];
    public static double[][] matrizCriterio3 = new double[4][4];

    public static double[][] matrizSomatorios = new double[4][4];
    public static double[][] mPrioridadeRelativa = new double[N_CRITERIOS + 1][N_MATRIZES];

    public static double[][] matrizNormalizadaCriterios = new double[3][3];
    public static double[][] matrizNormalizada1 = new double[4][4];
    public static double[][] matrizNormalizada2 = new double[4][4];
    public static double[][] matrizNormalizada3 = new double[4][4];

    public static void main(String[] args) throws FileNotFoundException {
        int nLinhas, op;
        do {
            op = menu();
            switch (op) {
                case 1:
                    nLinhas = 0;
                    nLinhas = LerFicheiroInput(nLinhas, v_criterios, mc_criterios, v_criterio1, matrizCriterio1, v_criterio2, matrizCriterio2, v_criterio3, matrizCriterio3);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    matrizSomatorios = criarMatrizSomatorios(matrizSomatorios, mc_criterios, matrizCriterio1, matrizCriterio2, matrizCriterio3);
                    normalizarMatrizes(matrizSomatorios, matrizNormalizadaCriterios, matrizNormalizada1, matrizNormalizada2, matrizNormalizada3);
                    mPrioridadeRelativa = prioridadeRelativa(mPrioridadeRelativa, matrizNormalizadaCriterios, matrizNormalizada1, matrizNormalizada2, matrizNormalizada3);
                    verificarConsistenciaAprox();selecaoOutput();
                    break;
                case 2:
                    nLinhas = 0;
                    nLinhas = LerFicheiroInput(nLinhas, v_criterios, mc_criterios, v_criterio1, matrizCriterio1, v_criterio2, matrizCriterio2, v_criterio3, matrizCriterio3);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    matrizSomatorios = criarMatrizSomatorios(matrizSomatorios, mc_criterios, matrizCriterio1, matrizCriterio2, matrizCriterio3);
                    normalizarMatrizes(matrizSomatorios, matrizNormalizadaCriterios, matrizNormalizada1, matrizNormalizada2, matrizNormalizada3);
                    verificarConsistenciaExato();
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

    private static double StringToDouble(String[] temp, int j) {
        if (temp[j].contains("/")) {
            String[] tempDiv = temp[j].split("/");
            return Double.parseDouble(tempDiv[0]) / Double.parseDouble(tempDiv[1]);
        } else {
            return Double.parseDouble(temp[j]);
        }
    }

    public static double[][] arredondar(double[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = (double) Math.round(matriz[i][j] * 100) / 100;
            }
        }
        return matriz;
    }

    /*CASE 1*/
    public static double[][] criarMatrizSomatorios(double[][] matrizSomatorios, double[][] mc_criterios, double[][] matrizCriterio1, double[][] matrizCriterio2, double[][] matrizCriterio3) {
        matrizSomatorios[0] = somatoriosColunas(mc_criterios);
        matrizSomatorios[1] = somatoriosColunas(matrizCriterio1);
        matrizSomatorios[2] = somatoriosColunas(matrizCriterio2);
        matrizSomatorios[3] = somatoriosColunas(matrizCriterio3);
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

    public static void normalizarMatrizes(double[][] matrizSomatorios, double[][] matrizNormalizadaCriterios, double[][] matrizNormalizada1, double[][] matrizNormalizada2, double[][] matrizNormalizada3) {
        double[][] matrizTemp;
        for (int nMatriz = 0; nMatriz < N_MATRIZES; nMatriz++) {
            switch (nMatriz) {
                case 0:
                    matrizTemp = mc_criterios;
                    matrizNormalizadaCriterios = normalizar(matrizSomatorios, matrizTemp, matrizNormalizadaCriterios, nMatriz);
                    break;
                case 1:
                    matrizTemp = matrizCriterio1;
                    matrizNormalizada1 = normalizar(matrizSomatorios, matrizTemp, matrizNormalizada1, nMatriz);
                    break;
                case 2:
                    matrizTemp = matrizCriterio2;
                    matrizNormalizada2 = normalizar(matrizSomatorios, matrizTemp, matrizNormalizada2, nMatriz);
                    break;
                case 3:
                    matrizTemp = matrizCriterio3;
                    matrizNormalizada3 = normalizar(matrizSomatorios, matrizTemp, matrizNormalizada3, nMatriz);
                    break;
                default:
                    break;
            }
        }
    }

    public static double[][] normalizar(double[][] matrizSomatorios, double[][] matriz, double[][] matrizNormalizada, int nMatriz) {

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matrizNormalizada[j][i] = matriz[j][i] / matrizSomatorios[nMatriz][i];
            }
        }
        return matrizNormalizada;
    }

    public static double[][] prioridadeRelativa(double[][] mPrioridadeRelativa, double[][] matrizNormalizadaCriterios, double[][] matrizNormalizada1, double[][] matrizNormalizada2, double[][] matrizNormalizada3) {
        double[][] matrizTemp;
        for (int nMatriz = 0; nMatriz < N_MATRIZES; nMatriz++) {
            switch (nMatriz) {
                case 0:
                    matrizTemp = matrizNormalizadaCriterios;
                    mPrioridadeRelativa = prioridadeLinhas(matrizTemp, nMatriz, mPrioridadeRelativa);
                    break;
                case 1:
                    matrizTemp = matrizNormalizada1;
                    mPrioridadeRelativa = prioridadeLinhas(matrizTemp, nMatriz, mPrioridadeRelativa);
                    break;
                case 2:
                    matrizTemp = matrizNormalizada2;
                    mPrioridadeRelativa = prioridadeLinhas(matrizTemp, nMatriz, mPrioridadeRelativa);
                    break;
                case 3:
                    matrizTemp = matrizNormalizada3;
                    mPrioridadeRelativa = prioridadeLinhas(matrizTemp, nMatriz, mPrioridadeRelativa);
                    break;
                default:
                    break;
            }
        }
        return arredondar(mPrioridadeRelativa);
    }

    public static double[][] prioridadeLinhas(double[][] matriz, int nMatriz, double[][] mPrioridadeRelativa) {
        double somaTemp = 0;
        int i, j;
        for (i = 0; i < matriz.length; i++) {
            for (j = 0; j < matriz[i].length; j++) {
                somaTemp = somaTemp + matriz[i][j];
            }
            mPrioridadeRelativa[i][nMatriz] = somaTemp / matriz[i].length;
            somaTemp = 0;
        }
        return mPrioridadeRelativa;
    }
    /*Verificação da consistencia de forma aproximada*/
    public static void verificarConsistenciaAprox(){
        double[] RCValues;
        RCValues=RCManualCheck();
        for(int i=0;i<RCValues.length;i++){
            if(RCValues[i]>0.1){
                System.out.println("Os valores das prioridades relativas da "+(i+1)+" inserida no input não são consistentes, RC:"+RCValues[i]);
            }else{
                System.out.println("Os valores das prioridades relativas da "+(i+1)+" inserida no input são consistentes, RC:"+RCValues[i]);
            }
        }
    }
    
    public static double[] RCManualCheck() {
        double[][] cPrioridade;double[] RCValues = new double[N_MATRIZES];
        for (int nMatriz = 0; nMatriz < N_MATRIZES; nMatriz++) {
            switch (nMatriz) {
                case 0:
                    cPrioridade = new double[mc_criterios.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz] = determinarManualRC(mc_criterios, cPrioridade, nMatriz);
                    break;
                case 1:
                    cPrioridade = new double[matrizCriterio1.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz] = determinarManualRC(matrizCriterio1, cPrioridade, nMatriz);
                    break;
                case 2:
                    cPrioridade = new double[matrizCriterio2.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz] = determinarManualRC(matrizCriterio2, cPrioridade, nMatriz);
                    break;
                case 3:
                    cPrioridade = new double[matrizCriterio3.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz] = determinarManualRC(matrizCriterio3, cPrioridade, nMatriz);
                    break;
                default:
                    break;
            }
        }
        return RCValues;
    }
    
    public static double determinarManualRC(double[][] matriz,double[][] cPrioridade, int nMatriz){
        double RC;double lambdaMax=0;double IC,IR;
        lambdaMax=determinarLambdaMax(lambdaMax,matriz,cPrioridade,nMatriz);
        IC=(lambdaMax-matriz.length)/(matriz.length-1);
        IR=RandomConsistency[matriz.length-1];
        RC=IC/IR;
        return RC;
    }
    
    public static double[][] encontrarColuna(double[][] mPrioridadeRelativa, double[][] cPrioridade, int nMatriz) {
        if (nMatriz == 0) {
            for (int i = 0; i < mPrioridadeRelativa.length-1; i++) {
                cPrioridade[i][0] = mPrioridadeRelativa[i][nMatriz];
            }
        } else if (nMatriz != 0) {
            for (int i = 0; i < mPrioridadeRelativa.length; i++) {
                cPrioridade[i][0] = mPrioridadeRelativa[i][nMatriz];
            }
        }
        return cPrioridade;
    }

    public static double determinarLambdaMax(double lambdaMax,double[][] matriz, double[][] cPrioridade, int nMatriz) {
        double[][] matrizTemp;
        matrizTemp=calcularMultiplicacao(matriz,cPrioridade);
        lambdaMax=calcularDiv(matrizTemp,cPrioridade,lambdaMax);
        return lambdaMax;
    }
    
    public static double[][] calcularMultiplicacao(double[][] matriz1,double[][] matriz2){
        double[][] matrizTemp=new double[matriz1.length][matriz2[0].length];
        for(int i=0;i<matriz1.length;i++){
            for(int j=0;j<matriz2[0].length;j++){
                for (int k = 0; k < matriz1[i].length; k++) { 
                matrizTemp[i][j]+=(matriz1[i][k]*matriz2[k][j]);
                }
            }
        }
        return matrizTemp;
    }
    
    public static double calcularDiv(double[][] matrizTemp,double[][] cPrioridade,double lambdaMax){
        double somaTemp=0;
        for(int i=0;i<matrizTemp.length;i++){
            somaTemp+=matrizTemp[i][0]/cPrioridade[i][0];
        }
        lambdaMax=somaTemp/matrizTemp.length;
        return lambdaMax;
    }
    /*FIM da Verificação de consistencia por métodos aproximados*/
    
    /*Verificação dos vetores proprios por método exato*/
    public static void verificarConsistenciaExato(){
        double[] RCValues;
        RCValues=RCAutoCheck();
        for(int i=0;i<RCValues.length;i++){
            if(RCValues[i]>0.1){
                System.out.println("Os valores das prioridades relativas da "+(i+1)+" inserida no input não são consistentes, RC:"+RCValues[i]);
            }else{
                System.out.println("Os valores das prioridades relativas da "+(i+1)+" inserida no input são consistentes, RC:"+RCValues[i]);
            }
        }
    }
    
    public static double[] RCAutoCheck(){
        double[] RCValues = new double[N_MATRIZES];Matrix matriz;
        double[][] matrizValores=null;
        for (int nMatriz = 0; nMatriz < N_MATRIZES; nMatriz++) {
            switch (nMatriz) {
                case 0:
                    matriz =new Basic2DMatrix(mc_criterios);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz] = determinarAutoRC(mc_criterios, matrizValores);
                    break;
                case 1:
                    matriz =new Basic2DMatrix(matrizCriterio1);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz] = determinarAutoRC(matrizCriterio1, matrizValores);
                    break;
                case 2:
                    matriz =new Basic2DMatrix(matrizCriterio2);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz] = determinarAutoRC(matrizCriterio2, matrizValores);
                    break;
                case 3:
                    matriz =new Basic2DMatrix(matrizCriterio3);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz] = determinarAutoRC(matrizCriterio3, matrizValores);
                    break;
                default:
                    break;
            }
        }
        return RCValues;
    }
    
    public static double[][] decompor(Matrix matriz,double[][] matrizValores){
        EigenDecompositor eigenD=new EigenDecompositor(matriz);
        Matrix[] matrizDecomposta=eigenD.decompose();
        matrizValores=matrizDecomposta[1].toDenseMatrix().toArray();
        return matrizValores;
    }
    
    public static double determinarAutoRC(double[][] matriz,double[][] matrizValoresLambda){
        double RC;double lambdaMax=0;double IC,IR;
        lambdaMax=encontrarLambdaMax(matrizValoresLambda,lambdaMax);
        IC=(lambdaMax-matriz.length)/(matriz.length-1);
        IR=RandomConsistency[matriz.length-1];
        RC=IC/IR;
        return RC;
    }
    
    public static double encontrarLambdaMax(double[][] matrizValoresLambda,double lambdaMax){
        double valorMaxTemp=matrizValoresLambda[0][0];
        for(int i=0;i<matrizValoresLambda.length;i++){
            for(int j=0;j<matrizValoresLambda[i].length;j++){
                if(matrizValoresLambda[i][j]>valorMaxTemp){
                    valorMaxTemp=matrizValoresLambda[i][j];
                }
            }
        }
        lambdaMax=valorMaxTemp;
        return lambdaMax;
    }
    /*FIM da verificação dos vetores por métodos exatos*/
    
    public static void selecaoOutput(/*double[][] mPrioridadeRelativa*/){
        double escolhas[][];
        escolhas=calcularEscolhas();
        //encontrarMelhorEscolha();
        
    }

    private static double[][] calcularEscolhas() {
        double[][] escolhas;
        double[][] matrizPrioridades=new double[mc_criterios.length][1];
        double[][] matrizPrioridadesCriterios=new double[mPrioridadeRelativa.length][mPrioridadeRelativa.length-1];
        for(int i=0;i<mc_criterios.length;i++){
            matrizPrioridades[i][0]=mPrioridadeRelativa[i][0];
        }
        for(int i=0;i<matrizPrioridadesCriterios.length;i++){
            for(int j=1;j<matrizPrioridadesCriterios[i].length+1;j++){
                matrizPrioridadesCriterios[i][j-1]=mPrioridadeRelativa[i][j];
            }
        }
        escolhas=calcularMultiplicacao(matrizPrioridadesCriterios,matrizPrioridades);
        System.out.println("Teste");
        return escolhas;
    }
}