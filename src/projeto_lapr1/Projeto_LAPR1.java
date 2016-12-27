/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projeto_lapr1;

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
public class Projeto_LAPR1 {

    public static Scanner sc = new Scanner(System.in);
    public static double[] RandomConsistency = {0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.42};/*Vetor Randômio até ao n=8*/
    public static final int N_MATRIZES = 4;
    public static final int N_CRITERIOS = 3;
    public static final int N_OBJETOS = 4;

    public static String[] v_criterios = new String[4];
    public static String[] v_criterio1 = new String[5];
    public static String[] v_criterio2 = new String[5];
    public static String[] v_criterio3 = new String[5];

    public static double[][] mc_criterios = new double[N_CRITERIOS][N_CRITERIOS];
    public static double[][] matrizCriterio1 = new double[N_OBJETOS][N_OBJETOS];
    public static double[][] matrizCriterio2 = new double[N_OBJETOS][N_OBJETOS];
    public static double[][] matrizCriterio3 = new double[N_OBJETOS][N_OBJETOS];

    public static double[][] matrizSomatorios = new double[4][4];
    public static double[][] mPrioridadeRelativa = new double[N_CRITERIOS + 1][N_MATRIZES];

    public static double[][] matrizNormalizadaCriterios = new double[N_CRITERIOS][N_CRITERIOS];
    public static double[][] matrizNormalizada1 = new double[N_OBJETOS][N_OBJETOS];
    public static double[][] matrizNormalizada2 = new double[N_OBJETOS][N_OBJETOS];
    public static double[][] matrizNormalizada3 = new double[N_OBJETOS][N_OBJETOS];

    public static void main(String[] args) throws FileNotFoundException {
        int nLinhas, op;double[][] RCValues=null;String Input=args[0];String Output = args[1];
        do {
            op = menu();
            switch (op) {
                case 1:
                    nLinhas = 0;
                    nLinhas = LerFicheiroInput(Input,nLinhas, v_criterios, mc_criterios, v_criterio1, matrizCriterio1, v_criterio2, matrizCriterio2, v_criterio3, matrizCriterio3);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    matrizSomatorios = criarMatrizSomatorios(matrizSomatorios, mc_criterios, matrizCriterio1, matrizCriterio2, matrizCriterio3);
                    normalizarMatrizes(matrizSomatorios, matrizNormalizadaCriterios, matrizNormalizada1, matrizNormalizada2, matrizNormalizada3);
                    mPrioridadeRelativa = prioridadeRelativa(mPrioridadeRelativa, matrizNormalizadaCriterios, matrizNormalizada1, matrizNormalizada2, matrizNormalizada3);
                    RCValues=verificarConsistencia(op,RCValues);selecaoOutput(Output,op,RCValues);
                    break;
                case 2:
                    nLinhas = 0;
                    nLinhas = LerFicheiroInput(Input,nLinhas, v_criterios, mc_criterios, v_criterio1, matrizCriterio1, v_criterio2, matrizCriterio2, v_criterio3, matrizCriterio3);
                    System.out.println(nLinhas + " linhas de info relevante lidas");
                    matrizSomatorios = criarMatrizSomatorios(matrizSomatorios, mc_criterios, matrizCriterio1, matrizCriterio2, matrizCriterio3);
                    normalizarMatrizes(matrizSomatorios, matrizNormalizadaCriterios, matrizNormalizada1, matrizNormalizada2, matrizNormalizada3);
                    mPrioridadeRelativa = prioridadeRelativa(mPrioridadeRelativa, matrizNormalizadaCriterios, matrizNormalizada1, matrizNormalizada2, matrizNormalizada3);
                    RCValues=verificarConsistencia(op,RCValues);selecaoOutput(Output,op,RCValues);
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

    public static int LerFicheiroInput(String Input,int nLinhas, String[] v_criterios, double[][] mc_criterios, String[] v_criterio1, double[][] matrizCriterio1, String[] v_criterio2, double[][] matrizCriterio2, String[] v_criterio3, double[][] matrizCriterio3) throws FileNotFoundException {
        /*System.out.println("Qual o nome do Input? (ex:DadosInput.txt)");
        String nomeFich=sc.nextLine();*/
        Scanner readFile = new Scanner(new File(Input));
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
    
    /**
     * Dar output a uma matriz pela consola
     * @param matriz matriz para o Output
     */
    public static void printMatriz(double[][] matriz){
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%5s", String.valueOf(matriz[i][j]));
            }
        System.out.println("");
        }
    }

    private static double StringToDouble(String[] temp, int j) {
        if (temp[j].contains("/")) {
            String[] tempDiv = temp[j].split("/");
            return Double.parseDouble(tempDiv[0]) / Double.parseDouble(tempDiv[1]);
        } else {
            return Double.parseDouble(temp[j]);
        }
    }

    /**
     * Arredondar valores presentes em matrizes
     * @param matriz matriz que se quer arredondar
     * @return
     */
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

    /**
     * Calcular soamtorios
     * @param matriz matriz chamada
     * @return
     */
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
    /*Normalizar matrizes*/
    public static double[][] normalizar(double[][] matrizSomatorios, double[][] matriz, double[][] matrizNormalizada, int nMatriz) {

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matrizNormalizada[j][i] = matriz[j][i] / matrizSomatorios[nMatriz][i];
            }
        }
        return matrizNormalizada;
    }
    /*Calcular prioridade relativa*/
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
        return mPrioridadeRelativa;
    }

    /**
     * Calcular as prioridades
     * @param matriz matriz chamada
     * @param nMatriz flag que identifica a matriz
     * @param mPrioridadeRelativa matriz das prioridades compostas
     * @return
     */
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
    
    /**
     * Verificar a consistencia das matrizes/INPUT
     * @param op flag da escolha do utilizador
     * @param RCValues matriz de dados que aloja na primeira coluna os valores do RC, na segunda o maior valor p´roprio e na terceira o IR
     * @return
     */
    public static double[][] verificarConsistencia(int op,double[][] RCValues){
        if(op==1){
            RCValues=RCManualCheck(); 
        }else if(op==2){
            RCValues=RCAutoCheck();
        }
        System.out.println(" ");
        return RCValues;
    }
    /*Verificação da consistencia de forma aproximada*/

    /**
     * Calcular RC/IR/Valores Próprios de forma aproximada
     * @return
     */

    public static double[][] RCManualCheck() {
        double[][] cPrioridade;double[][] RCValues = new double[N_MATRIZES][3];
        for (int nMatriz = 0; nMatriz < N_MATRIZES; nMatriz++) {
            switch (nMatriz) {
                case 0:
                    cPrioridade = new double[mc_criterios.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz][0] = determinarManualRC(mc_criterios, cPrioridade, nMatriz,RCValues);
                    break;
                case 1:
                    cPrioridade = new double[matrizCriterio1.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz][0] = determinarManualRC(matrizCriterio1, cPrioridade, nMatriz,RCValues);
                    break;
                case 2:
                    cPrioridade = new double[matrizCriterio2.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz][0] = determinarManualRC(matrizCriterio2, cPrioridade, nMatriz,RCValues);
                    break;
                case 3:
                    cPrioridade = new double[matrizCriterio3.length][1];
                    cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                    RCValues[nMatriz][0] = determinarManualRC(matrizCriterio3, cPrioridade, nMatriz,RCValues);
                    break;
                default:
                    break;
            }
        }
        return RCValues;
    }
    
    /**
     *
     * @param matriz matriz chamada
     * @param cPrioridade coluna onde está guardados os dados das prioridades
     * @param nMatriz flag que identifica a matriz
     * @param RCValues matriz de dados que aloja na primeira coluna os valores do RC, na segunda o maior valor p´roprio e na terceira o IR
     * @return
     */
    public static double determinarManualRC(double[][] matriz,double[][] cPrioridade, int nMatriz,double[][] RCValues){
        double RC;double lambdaMax=0;double IC,IR;
        lambdaMax=determinarLambdaMax(lambdaMax,matriz,cPrioridade,nMatriz);
        RCValues[nMatriz][1]=lambdaMax;
        IC=(lambdaMax-matriz.length)/(matriz.length-1);
        IR=RandomConsistency[matriz.length-1];
        RCValues[nMatriz][2]=IR;
        RC=IC/IR;
        return RC;
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

    /**
     * Determinação do maior valor Proprio
     * @param lambdaMax variavel que guarda os valor proprio maior
     * @param matriz matriz que foi chamada
     * @param cPrioridade coluna em que fica guardada os dados das prioridades
     * @param nMatriz Flag que identifica a matriz
     * @return
     */
    public static double determinarLambdaMax(double lambdaMax,double[][] matriz, double[][] cPrioridade, int nMatriz) {
        double[][] matrizTemp;
        matrizTemp=calcularMultiplicacao(matriz,cPrioridade);
        lambdaMax=calcularDiv(matrizTemp,cPrioridade,lambdaMax);
        return lambdaMax;
    }
    
    private static double[][] calcularMultiplicacao(double[][] matriz1,double[][] matriz2){
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
    
    private static double calcularDiv(double[][] matrizTemp,double[][] cPrioridade,double lambdaMax){
        double somaTemp=0;
        for(int i=0;i<matrizTemp.length;i++){
            somaTemp+=matrizTemp[i][0]/cPrioridade[i][0];
        }
        lambdaMax=somaTemp/matrizTemp.length;
        return lambdaMax;
    }
    /*FIM da Verificação de consistencia por métodos aproximados*/
    
    /*Verificação dos vetores proprios por método exato*/ 

    /**
     * Calcular RC comvalores Exatos/Eigen Decomposition
     * @return
     */
    public static double[][] RCAutoCheck(){
        double[][] RCValues = new double[N_MATRIZES][3];Matrix matriz;
        double[][] matrizValores=null;
        for (int nMatriz = 0; nMatriz < N_MATRIZES; nMatriz++) {
            switch (nMatriz) {
                case 0:
                    matriz =new Basic2DMatrix(mc_criterios);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz][0] = determinarAutoRC(mc_criterios, matrizValores,nMatriz,RCValues);
                    break;
                case 1:
                    matriz =new Basic2DMatrix(matrizCriterio1);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz][0] = determinarAutoRC(matrizCriterio1, matrizValores,nMatriz,RCValues);
                    break;
                case 2:
                    matriz =new Basic2DMatrix(matrizCriterio2);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz][0] = determinarAutoRC(matrizCriterio2, matrizValores,nMatriz,RCValues);
                    break;
                case 3:
                    matriz =new Basic2DMatrix(matrizCriterio3);
                    matrizValores=decompor(matriz,matrizValores);
                    RCValues[nMatriz][0] = determinarAutoRC(matrizCriterio3, matrizValores,nMatriz,RCValues);
                    break;
                default:
                    break;
            }
        }
        return RCValues;
    }
    
    /**
     * Aplicar Eigen Decomposition
     * @param matriz matriz que foi chamada
     * @param matrizValores matriz que guarda os valores próprios
     * @return
     */
    public static double[][] decompor(Matrix matriz,double[][] matrizValores){
        EigenDecompositor eigenD=new EigenDecompositor(matriz);
        Matrix[] matrizDecomposta=eigenD.decompose();
        matrizValores=matrizDecomposta[1].toDenseMatrix().toArray();
        return matrizValores;
    }
    
    /**
     * Determina o maior valor proprio, RC, IR
     * @param matriz matriz chamada
     * @param matrizValoresLambda matriz Valores P´rprios
     * @param nMatriz Flag identificadora da matriz que está a sofrer operações
     * @param RCValues matriz de dados que aloja na primeira coluna os valores do RC, na segunda o maior valor p´roprio e na terceira o IR
     * @return
     */
    public static double determinarAutoRC(double[][] matriz,double[][] matrizValoresLambda,int nMatriz,double[][] RCValues){
        double RC;double lambdaMax=0;double IC,IR;
        lambdaMax=encontrarLambdaMax(matrizValoresLambda,lambdaMax);
        RCValues[nMatriz][1]=lambdaMax;
        IC=(lambdaMax-matriz.length)/(matriz.length-1);
        IR=RandomConsistency[matriz.length-1];
        RCValues[nMatriz][2]=IR;
        RC=IC/IR;
        return RC;
    }
    
    /**
     * Percorre a matriz dos valores próprios pelo maior valor
     * @param matrizValoresLambda matriz Valores Próprios
     * @param lambdaMax variavel do maior valor próprio
     * @return
     */
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
    
    private static void printMatrizTotalInput(String[][] matrizTotal,int nLinhasOutput){
        for(int i=0;i<nLinhasOutput/2;i++){
            for(int j=0;j<matrizTotal[i].length;j++){
                System.out.printf("%20s", matrizTotal[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }
    
    /**
     * HUB do Output : consola e ficheiro
     * @param op flag da decisao do utilizador
     * @param RCValues matriz de dados que aloja na primeira coluna os valores do RC, na segunda o maior valor p´roprio e na terceira o IR
     * @throws FileNotFoundException
     */
    public static void selecaoOutput(String Output,int op,double[][] RCValues) throws FileNotFoundException{
        double escolhas[][];String[][] matrizTotal=new String[100][6];int nLinhasOutput=0;
        nLinhasOutput=juntarDados(matrizTotal,nLinhasOutput,op);
        matrizTotal=eliminarNull(matrizTotal);
        printMatrizTotalInput(matrizTotal,nLinhasOutput);
        for(int i=0;i<RCValues.length;i++){
            if(RCValues[i][0]>0.1){
                System.out.println("Os valores das prioridades relativas da "+(i+1)+"ªmatriz inserida no input não são consistentes, RC:"+RCValues[i][0]);
            }else if(RCValues[i][0]<=0.1){
                System.out.println("Os valores das prioridades relativas da "+(i+1)+"ªmatriz inserida no input são consistentes, RC:"+RCValues[i][0]);
            }
        }
        escolhas=calcularEscolhas();
        System.out.println(" ");
        System.out.println("Escolhas:");
        printMatriz(escolhas);
        System.out.println("A melhor alternativa segundo os critérios é a alternativa "+encontrarMelhorEscolha(escolhas));
        guardarOutputTotalTXT(Output,matrizTotal,escolhas,nLinhasOutput,encontrarMelhorEscolha(escolhas),RCValues);
    }

    /**
     * Calcular a pontuação de todas as alternativas
     * @return
     */
    public static double[][] calcularEscolhas() {
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
        return escolhas;
    }
    
    /**
     * Encontrar a alternativa com maior pontuação
     * @param escolhas matriz de escolhas
     * @return
     */
    public static int encontrarMelhorEscolha(double[][] escolhas){
        int melhorEscolha=-1;
        double melhorPontuacao=escolhas[0][0];
        for(int i=1;i<escolhas.length;i++){
            if(escolhas[i][0]>melhorPontuacao){
                melhorPontuacao=escolhas[i][0];
                melhorEscolha=i+1;
            }
        }
        return melhorEscolha;
    }
    
    /**
     * Juntar todos os dados lidos e calculados para uma matriz que servirá de Output
     * @param matrizTotal matriz de dados totais
     * @param nLinhasOutput numero de linhas usadas para Output
     * @param op flag de escolha do utilizador
     * @return
     */
    public static int juntarDados(String[][] matrizTotal,int nLinhasOutput,int op){
        int nColuna=0;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterios,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(mc_criterios,op),nLinhasOutput);
        nLinhasOutput++;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterio1,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(matrizCriterio1,op),nLinhasOutput);
        nLinhasOutput++;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterio2,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(matrizCriterio2,op),nLinhasOutput);
        nLinhasOutput++;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterio3,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(matrizCriterio3,op),nLinhasOutput);
        nLinhasOutput++;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterios,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(matrizNormalizadaCriterios,op),nLinhasOutput);
        nColuna=adicionarVetorPrioridades(matrizTotal,DoubleToString(mPrioridadeRelativa,op),nLinhasOutput,nColuna);
        nLinhasOutput++;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterio1,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(matrizNormalizada1,op),nLinhasOutput);
        nColuna=adicionarVetorPrioridades(matrizTotal,DoubleToString(mPrioridadeRelativa,op),nLinhasOutput,nColuna);
        nLinhasOutput++;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterio2,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(matrizNormalizada2,op),nLinhasOutput);
        nColuna=adicionarVetorPrioridades(matrizTotal,DoubleToString(mPrioridadeRelativa,op),nLinhasOutput,nColuna);
        nLinhasOutput++;
        nLinhasOutput=adicionarDadosCabecalho(matrizTotal,v_criterio3,nLinhasOutput);
        nLinhasOutput=adicionarDadosMatrizes(matrizTotal,DoubleToString(matrizNormalizada3,op),nLinhasOutput);
        nColuna=adicionarVetorPrioridades(matrizTotal,DoubleToString(mPrioridadeRelativa,op),nLinhasOutput,nColuna);
        return nLinhasOutput;
    }
    
    private static int adicionarDadosCabecalho(String[][] matrizTotal,String[] matriz,int nLinhasOutput){
        for(int i=0;i<matriz.length;i++){
                matrizTotal[nLinhasOutput][i]=matriz[i];
                matrizTotal[nLinhasOutput+i][0]=matriz[i];
        }
        nLinhasOutput++;
        return nLinhasOutput;
    }
    
    private static int adicionarDadosMatrizes(String[][] matrizTotal,String[][] matriz,int nLinhasOutput){
        for(int i=0;i<matriz.length;i++){
            for(int j=0;j<matriz[i].length;j++){
                matrizTotal[nLinhasOutput][j+1]=matriz[i][j];
            }
        nLinhasOutput++;    
        }
        return nLinhasOutput;
    }
    
    private static int adicionarVetorPrioridades(String[][] matrizTotal,String[][] matrizPrioridades,int nLinhasOutput,int nColuna){
        if(nColuna==0){
            for(int i=0;i<matrizPrioridades.length;i++){
                if(i==0){
                    matrizTotal[nLinhasOutput-matrizPrioridades[nColuna].length+i][N_OBJETOS+1]="Prioridade Relativa";
                }else{
                    matrizTotal[nLinhasOutput-matrizPrioridades[nColuna].length+i][N_OBJETOS+1]=matrizPrioridades[i-1][nColuna];
                }
            }
            nColuna++;
        }else if(nColuna!=0){
           for(int i=-1;i<matrizPrioridades.length;i++){
                if(i==-1){
                    matrizTotal[nLinhasOutput-matrizPrioridades[nColuna].length+i][N_OBJETOS+1]="Prioridade Relativa";
                }else{
                    matrizTotal[nLinhasOutput-matrizPrioridades[nColuna].length+i][N_OBJETOS+1]=matrizPrioridades[i][nColuna];
                }
           }
            nColuna++; 
        }
        return nColuna;
    }
        
    /**
     * Converter matriz Double para String
     * @param matriz Qualquer matriz que seja chamada
     * @param op flag para identificar a necessidade de arredondar valores
     * @return
     */
    public static String[][] DoubleToString(double[][] matriz,int op){
        if(op==1){
            matriz=arredondar(matriz);
        }
        String[][] matrizConvert=new String [matriz.length][matriz[0].length];
        for(int i =0;i<matriz.length;i++){
            for(int j=0;j<matriz[i].length;j++){
                matrizConvert[i][j]=String.valueOf(matriz[i][j]);
            }
        }
        return matrizConvert;
    }
    
    /**
     * Eliminar espaços que sejam null na matriz
     * @param matrizTotal matriz total de dados
     * @return
     */
    public static String[][] eliminarNull(String[][] matrizTotal){
        for(int i=0;i<matrizTotal.length;i++){
            for(int j=0;j<matrizTotal[i].length;j++){
                if(matrizTotal[i][j]==null){
                    matrizTotal[i][j]=" ";
                }
            }
        }
        return matrizTotal;
    }
    
    /**
     * OUTPUT PARA TXT
     * @param matrizTotal matriz dos dados totais
     * @param escolhas matriz que guarda os dados das pontuações das escolhas
     * @param nLinhasOutput quantidade de linhas usadas para o Output
     * @param melhorEscolha variavel que guarda a melhor escolha
     * @param RCValues matriz de dados que aloja na primeira coluna os valores do RC, na segunda o maior valor p´roprio e na terceira o IR
     * @throws FileNotFoundException
     */
    public static void guardarOutputTotalTXT(String Output,String[][] matrizTotal,double[][] escolhas,int nLinhasOutput,int melhorEscolha,double[][] RCValues) throws FileNotFoundException{
        /*System.out.println("Qual o nome do Output? (ex:DadosOutput.txt)");
        String nomeFich=sc.nextLine();*/
        Formatter out = new Formatter(new File(Output));
        for (int i = 0; i < nLinhasOutput; i++) {          
            for (int j = 0; j < matrizTotal[i].length; j++) {
                out.format("%20s", matrizTotal[i][j]);
            }
            out.format("%n");
        }
        out.format("%25s","RC");out.format("%25s","Valor Próprio Máx"); out.format("%25s","IR");
        out.format("%n");
        for(int a=0;a<RCValues.length;a++){
           for(int b=0;b<RCValues[a].length;b++){
                out.format("%25s",(a+1)+" : "+(double)Math.round(RCValues[a][b]*1000)/1000);
           }
           out.format("%n");
        }
        
        out.format("%20s","Prioridade Composta");
        out.format("%n");
        /*out.format("%20s","MatrizCriterio1");out.format("%20s","MatrizCriterio2");out.format("%20s","MatrizCriterio3");
        out.format("%n");*/
        for(int a=0;a<mPrioridadeRelativa.length;a++){
            for(int b=1;b<mPrioridadeRelativa[a].length;b++){
                out.format("%20s"," | "+(double)Math.round(mPrioridadeRelativa[a][b]*1000)/1000);
            }
            out.format("%n");
        }
        out.format("%20s","Pontuação Final");
        out.format("%n");
        for(int a=0;a<escolhas.length;a++){
            for(int b=0;b<escolhas[a].length;b++){
                out.format("%20s","Alternativa "+(a+1)+": "+(double)Math.round(escolhas[a][b]*100)/100);
            }
            out.format("%n");
        }
        out.format("%n");
        out.format("A melhor alternativa é a alternativa: "+melhorEscolha);
        out.close();
    } 
}