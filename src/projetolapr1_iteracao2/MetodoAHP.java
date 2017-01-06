package projetolapr1_iteracao2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;
import org.la4j.Matrix;
import org.la4j.matrix.DenseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.decomposition.EigenDecompositor;

/**
 *
 * @author PC
 */
public class MetodoAHP {

    public static Scanner sc = new Scanner(System.in);
    public static double[] RandomConsistency = {0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.42};/*Vetor Randômio até ao n=8*/
    public static int N_CRITERIOS,N_ALTERNATIVAS;

    public static void main(String args[]/*int DecisaoLimiar e input e output*/) throws FileNotFoundException {
        String Input = "DadosInputAHP.txt",Output;
        String[][] matrizTotalInput = new String[50][100],m_cabecalhos=null;int nLinhas,op;
        double[][] matrizTotalCriterios=null,matrizSomatorios=null,matrizCriterios=null,matrizCriteriosNormalizada=null,matrizTotalNormalizacao=null,mPrioridadeRelativa=null,RCValues = null;
        do {
            op = menu();
            switch (op) {
                case 1:
                    nLinhas = 0;
                    nLinhas = LerFicheiroInput(Input, nLinhas, matrizTotalInput);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    N_CRITERIOS = encontrarNELEMENTOS(matrizTotalInput[0]);
                    N_ALTERNATIVAS=encontrarNELEMENTOS(matrizTotalInput[N_CRITERIOS+1]);
                    matrizCriterios=criarMatrizCriterios(matrizTotalInput,matrizCriterios,N_CRITERIOS);
                    m_cabecalhos=criarMatrizCabecalhos(matrizTotalInput,matrizCriterios,N_CRITERIOS,N_ALTERNATIVAS,nLinhas,m_cabecalhos);
                    matrizTotalCriterios=criarMatrizTotalCriterios(matrizTotalInput,matrizCriterios,matrizTotalCriterios,N_CRITERIOS,N_ALTERNATIVAS,nLinhas,m_cabecalhos);
                    matrizSomatorios=criarMatrizSomatorios(matrizSomatorios,matrizCriterios,matrizTotalCriterios,N_ALTERNATIVAS,N_CRITERIOS);
                    matrizCriteriosNormalizada=normalizar(matrizSomatorios,matrizCriterios,matrizCriteriosNormalizada,0);
                    matrizTotalNormalizacao=normalizarMatrizes(matrizSomatorios,matrizTotalCriterios,matrizCriterios,matrizCriteriosNormalizada,matrizTotalNormalizacao,N_CRITERIOS,N_ALTERNATIVAS);
                    mPrioridadeRelativa=prioridadeRelativa(mPrioridadeRelativa,matrizCriteriosNormalizada,matrizTotalNormalizacao,N_CRITERIOS,N_ALTERNATIVAS);
                    RCValues=verificarConsistencia(op,RCValues,mPrioridadeRelativa,matrizCriterios,matrizTotalCriterios);
                    break;
                case 2:
                    nLinhas = 0;
                    nLinhas = LerFicheiroInput(Input, nLinhas, matrizTotalInput);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    N_CRITERIOS = encontrarNELEMENTOS(matrizTotalInput[0]);
                    matrizCriterios=criarMatrizCriterios(matrizTotalInput,matrizCriterios,N_CRITERIOS);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção incorreta.");
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

    public static int LerFicheiroInput(String Input, int nLinhas, String[][] matrizTotalInput) throws FileNotFoundException {
        Scanner readFile = new Scanner(new File(Input));
        while (readFile.hasNext()) {
            String linhaDados = readFile.nextLine();
            if (linhaDados.length() > 0) {
                nLinhas = tratarInput(linhaDados, nLinhas, matrizTotalInput);
            }
        }
        readFile.close();
        return nLinhas;
    }

    public static int tratarInput(String linhaDados, int nLinhas, String[][] matrizTotalInput) {
        String temp[] = linhaDados.split(" +");// " +" Split por numero arbitrario de espaços
        for (int j = 0; j < temp.length; j++) {
            matrizTotalInput[nLinhas][j] = temp[j];
        }
        nLinhas++;
        return nLinhas;
    }
    
    public static int encontrarNELEMENTOS(String[] linha){
        int N_ELEMENTOS=0;
        for(int i=0;i<linha.length;i++){
            if(linha[i]!=null){
                N_ELEMENTOS++;
            }
        }
        return N_ELEMENTOS-1;
    }
    
    /*public static int encontrarNALTERNATIVAS(String[] linha){
        int N_ALTERNATIVAS=0;
        for(int i=0;i<linha.length;i++){
            if(linha[i]!=null){
                N_ALTERNATIVAS++;
            }
        }
        return N_ALTERNATIVAS-1;
    }*/
    
    public static double[][] criarMatrizCriterios(String[][] matrizInputTotal,double[][] matrizCriterios,int N_CRITERIOS){
        matrizCriterios=new double[N_CRITERIOS][N_CRITERIOS];
        for(int i =0;i<matrizCriterios.length;i++){
            for(int j=0;j<matrizCriterios[i].length;j++){
                matrizCriterios[i][j]=StringToDouble(matrizInputTotal[i+1][j]);
            }
        }
        return matrizCriterios;
    }
    
    public static double[][] criarMatrizTotalCriterios(String[][] matrizInputTotal,double[][] matrizCriterios,double[][] matrizTotalCriterios,int N_CRITERIOS,int N_ALTERNATIVAS,int nLinhas,String[][] m_cabecalhos){
        matrizTotalCriterios=new double[N_CRITERIOS*N_ALTERNATIVAS][N_ALTERNATIVAS];int c=0,p=0,i=0,j,k;m_cabecalhos=new String[N_CRITERIOS][N_ALTERNATIVAS+1];
            for(j=0;j<matrizTotalCriterios.length+matrizCriterios.length;j++){
                if(matrizInputTotal[j+(matrizCriterios.length+1)][0].contains("mc")!=true){
                    for(k=0;k<matrizTotalCriterios[c].length;k++){
                        matrizTotalCriterios[c][k]=StringToDouble(matrizInputTotal[j+(matrizCriterios.length+1)][k]);  
                    }
                    c++;
                } 
            }
        return matrizTotalCriterios;
    }
    
    public static String[][] criarMatrizCabecalhos(String[][] matrizInputTotal,double[][] matrizCriterios,int N_CRITERIOS,int N_ALTERNATIVAS,int nLinhas,String[][] m_cabecalhos){
        int p=0,j,k;m_cabecalhos=new String[N_CRITERIOS][N_ALTERNATIVAS+1];
            for(j=0;j<nLinhas-(matrizCriterios.length+1);j++){
                if(matrizInputTotal[j+(matrizCriterios.length+1)][0].contains("mc")){
                    for(k=0;k<m_cabecalhos[p].length;k++){
                        m_cabecalhos[p][k]=matrizInputTotal[j+(matrizCriterios.length+1)][k];
                    }
                    p++;
                }
            }
        return m_cabecalhos;
    }
    
    private static double StringToDouble(String N) {
        if (N.contains("/")) {
            String[] tempDiv = N.split("/");
            return Double.parseDouble(tempDiv[0]) / Double.parseDouble(tempDiv[1]);
        } else {
            return Double.parseDouble(N);
        }
    }
    
    public static double[][] criarMatrizSomatorios(double[][] matrizSomatorios,double[][] matrizCriterios,double[][] matrizTotalCriterios,int N_ALTERNATIVAS,int N_CRITERIOS){
        matrizSomatorios=new double[N_CRITERIOS+1][N_ALTERNATIVAS];
        matrizSomatorios[0] = somatoriosColunas(matrizCriterios);
        for(int i=0;i<N_CRITERIOS;i++){
            matrizSomatorios[i+1]=somatoriosColunas(identificarMatriz(matrizTotalCriterios,i,N_ALTERNATIVAS));
        }
        return matrizSomatorios;
    }
    
    public static double[][] identificarMatriz(double[][] matrizTotalCriterios,int i,int N_ALTERNATIVAS){
        double[][] matrizTemp=new double[N_ALTERNATIVAS][N_ALTERNATIVAS];
        for(int j=0;j<matrizTemp.length;j++){
            for(int k=0;k<matrizTemp[j].length;k++){
                matrizTemp[j][k]=matrizTotalCriterios[j+(i*N_ALTERNATIVAS)][k];
            }
        }
        return matrizTemp;
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

    public static double[][] normalizarMatrizes(double[][] matrizSomatorios,double[][] matrizTotalCriterios,double[][] matrizCriterios,double[][] matrizCriteriosNormalizada,double[][] matrizTotalNormalizacao,int N_CRITERIOS,int N_ALTERNATIVAS) {
        int nLinhasLidas=0;matrizTotalNormalizacao=new double[matrizTotalCriterios.length][matrizTotalCriterios[0].length];
        for (int nMatriz = 0; nMatriz < N_CRITERIOS; nMatriz++) {
            double[][] matrizTemp=identificarMatriz(matrizTotalCriterios,nMatriz,N_ALTERNATIVAS);
            double[][] matrizNormalizadaTemp=normalizar(matrizSomatorios,matrizTemp,matrizTotalNormalizacao,nMatriz+1);
            for(int i=0;i<matrizTemp.length;i++){
                for(int j=0;j<matrizTemp[i].length;j++){
                    matrizTotalNormalizacao[i+nLinhasLidas][j]=matrizNormalizadaTemp[i][j];
                }
            }
            nLinhasLidas = nLinhasLidas+matrizNormalizadaTemp.length;
        }
        return matrizTotalNormalizacao;
    }
    
    public static double[][] normalizar(double[][] matrizSomatorios, double[][] matriz, double[][] matrizNormalizada, int nMatriz) {
        matrizNormalizada=new double[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matrizNormalizada[j][i] = matriz[j][i] / matrizSomatorios[nMatriz][i];
            }
        }
        return matrizNormalizada;
    }
    
    public static double[][] prioridadeRelativa(double[][] mPrioridadeRelativa,double[][] matrizCriteriosNormalizada,double[][] matrizTotalNormalizacao,int N_CRITERIOS,int N_ALTERNATIVAS) {
        mPrioridadeRelativa=new double[N_CRITERIOS+1][N_ALTERNATIVAS];
        for (int nMatriz = 0; nMatriz < N_CRITERIOS+1; nMatriz++) {
                if(nMatriz==0){
                    mPrioridadeRelativa = prioridadeLinhas(matrizCriteriosNormalizada, nMatriz, mPrioridadeRelativa);
                }else{
                    mPrioridadeRelativa = prioridadeLinhas(identificarMatriz(matrizTotalNormalizacao,nMatriz-1,N_ALTERNATIVAS), nMatriz, mPrioridadeRelativa);
                }    
        }
        return mPrioridadeRelativa;
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
    
    public static double[][] verificarConsistencia(int op,double[][] RCValues,double[][] mPrioridadeRelativa,double[][] matrizCriterios,double[][] matrizTotalCriterios){
        if(op==1){
            RCValues=RCManualCheck(mPrioridadeRelativa,matrizCriterios,matrizTotalCriterios); 
        }else if(op==2){
            /*RCValues=RCAutoCheck();*/
        }
        System.out.println(" ");
        return RCValues;
    }
    
    public static double[][] RCManualCheck(double[][] mPrioridadeRelativa,double[][] matrizCriterios,double[][] matrizTotalCriterios) {
        double[][] cPrioridade;double[][] RCValues = new double[N_CRITERIOS+1][3];//RCValues[][3], o 3 refere-se ao numero de colunas: RC,Valor Proprio,IR
        for (int nMatriz = 0; nMatriz < N_CRITERIOS+1; nMatriz++) {
            if(nMatriz==0){       
                    cPrioridade = new double[N_CRITERIOS][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz][0] = determinarManualRC(matrizCriterios, cPrioridade, nMatriz,RCValues);
            }else{
                    cPrioridade = new double[N_ALTERNATIVAS][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz][0] = determinarManualRC(identificarMatriz(matrizTotalCriterios,nMatriz,N_ALTERNATIVAS), cPrioridade, nMatriz,RCValues);        
            }          
        }
        return RCValues;
    }

    private static double[][] encontrarColuna(double[][] mPrioridadeRelativa, double[][] cPrioridade, int nMatriz) {
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
    
    
}
