package projetolapr1_iteracao2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Scanner;
import org.la4j.Matrix;
import org.la4j.matrix.DenseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.decomposition.EigenDecompositor;

public class MetodoAHP {

    public static Scanner sc = new Scanner(System.in);
    public static double[] RandomConsistency = {0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.42};/*Vetor Randômio até ao n=8*/
    public static int N_CRITERIOS, N_ALTERNATIVAS;
    public static Calendar Data = Calendar.getInstance();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd_MM_yyyy HH.mm.ss");/*Windows nao permite : como caracter*/
    private static final String FILE_LOG_ERROS = SDF.format(Data.getTime()) + ".txt";
    public static String[] posicaoDadosIrrelevantes;
    public static String[][] matrizTotalInput = new String[50][100], m_cabecalhos = null;
    public static double[][] matrizTotalCriterios = null, matrizSomatorios = null, matrizCriterios = null, matrizCriteriosNormalizada = null, matrizTotalNormalizacao = null, mPrioridadeRelativa = null, RCValues = null;

    public static void main(/*String[] args*/double limiarCriterio,double limiarRC,String Input,String Output) throws FileNotFoundException {
        //String Input = "DadosInputAHP.txt", Output = "DadosOutputAHPIt2.txt";
        /*String[][] matrizTotalInput = new String[50][100], m_cabecalhos = null;*/
        int nLinhas, op;
        //double limiarCriterio = 0.2, limiarRC = 0.05;//Exemplo
        Formatter logErros = new Formatter(new File(FILE_LOG_ERROS));
        do {
            op = menu();
            nLinhas = 0;
            nLinhas = LerFicheiroInput(Input, nLinhas, matrizTotalInput, logErros);
            System.out.println(nLinhas + " linhas de info lidas");
            N_CRITERIOS = encontrarNELEMENTOS(matrizTotalInput[0]);
            N_ALTERNATIVAS = encontrarNELEMENTOS(matrizTotalInput[N_CRITERIOS + 1]);
            matrizCriterios = criarMatrizCriterios(matrizTotalInput, matrizCriterios, N_CRITERIOS,logErros);
            m_cabecalhos = criarMatrizCabecalhos(matrizTotalInput, matrizCriterios, N_CRITERIOS, N_ALTERNATIVAS, nLinhas, m_cabecalhos);
            matrizTotalCriterios = criarMatrizTotalCriterios(matrizTotalInput, matrizCriterios, matrizTotalCriterios, N_CRITERIOS, N_ALTERNATIVAS, nLinhas, m_cabecalhos,logErros);
            matrizSomatorios = criarMatrizSomatorios(matrizSomatorios, matrizCriterios, matrizTotalCriterios, N_ALTERNATIVAS, N_CRITERIOS);
            matrizCriteriosNormalizada = normalizar(matrizSomatorios, matrizCriterios, matrizCriteriosNormalizada, 0);
            matrizTotalNormalizacao = normalizarMatrizes(matrizSomatorios, matrizTotalCriterios, matrizCriterios, matrizCriteriosNormalizada, matrizTotalNormalizacao, N_CRITERIOS, N_ALTERNATIVAS);
            mPrioridadeRelativa = prioridadeRelativa(mPrioridadeRelativa, matrizCriteriosNormalizada, matrizTotalNormalizacao, N_CRITERIOS, N_ALTERNATIVAS);

            switch (op) {
                case 1:
                    RCValues = verificarConsistencia(op, RCValues, mPrioridadeRelativa, matrizCriterios, matrizTotalCriterios);
                    selecaoOutput(Output, op, RCValues, matrizCriterios, m_cabecalhos, matrizTotalCriterios, matrizSomatorios, matrizCriteriosNormalizada, matrizTotalNormalizacao, mPrioridadeRelativa, limiarRC, limiarCriterio);
                    break;
                case 2:
                    RCValues = verificarConsistencia(op, RCValues, mPrioridadeRelativa, matrizCriterios, matrizTotalCriterios);
                    selecaoOutput(Output, op, RCValues, matrizCriterios, m_cabecalhos, matrizTotalCriterios, matrizSomatorios, matrizCriteriosNormalizada, matrizTotalNormalizacao, mPrioridadeRelativa, limiarRC, limiarCriterio);
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
                + "\n FIM (Digite 0)"
                + "\n Qual é a opção?";
        System.out.printf("%n%s%n", texto);
        int op = sc.nextInt();
        sc.nextLine();
        return op;
    }

    public static int LerFicheiroInput(String Input, int nLinhas, String[][] matrizTotalInput, Formatter log) throws FileNotFoundException {
        try (Scanner readFile = new Scanner(new File(Input))) {
            while (readFile.hasNext()) {
                String linhaDados = readFile.nextLine();
                if (linhaDados.length() > 0) {
                    nLinhas = tratarInput(linhaDados, nLinhas, matrizTotalInput);
                }
            }
        } catch (FileNotFoundException erro) {
            log.format("Erro encontrado: ");
            log.format(erro.getMessage());
            System.err.println("Erro encontrado: " + erro.getMessage());//.getMessage vai buscar a detailedMessage da Exception
            log.close();
            System.exit(0);//.exit usas-se 0 porque é suposto darmos a execução como bem sucedida apesar do ficheiro nao ser encontrado, senao usamos valor 1
            throw erro;
        }
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

    public static int encontrarNELEMENTOS(String[] linha) {
        int N_ELEMENTOS = 0;
        for (int i = 0; i < linha.length; i++) {
            if (linha[i] != null) {
                N_ELEMENTOS++;
            }
        }
        return N_ELEMENTOS - 1;
    }

    public static void printMatriz(double[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%5s", String.valueOf(matriz[i][j]));
            }
            System.out.println("");
        }
    }

    public static double[][] criarMatrizCriterios(String[][] matrizInputTotal, double[][] matrizCriterios, int N_CRITERIOS,Formatter log) {
        matrizCriterios = new double[N_CRITERIOS][N_CRITERIOS];
        for (int i = 0; i < matrizCriterios.length; i++) {
            for (int j = 0; j < matrizCriterios[i].length; j++) {
                if (matrizInputTotal[i + 1][j].contains(".") || matrizInputTotal[i + 1][j].contains(",")) {
                    log.format("Formato errado (Posicao ("+(i+1)+","+(j+1)+")): ",matrizInputTotal[i + 1][j]);
                    System.out.println("Erro detetado, verificar Log");
                    log.close();
                    System.exit(0);
                } else {
                    matrizCriterios[i][j] = StringToDouble(matrizInputTotal[i + 1][j]);
                }
            }
        }
        return matrizCriterios;
    }

    public static double[][] criarMatrizTotalCriterios(String[][] matrizInputTotal, double[][] matrizCriterios, double[][] matrizTotalCriterios, int N_CRITERIOS, int N_ALTERNATIVAS, int nLinhas, String[][] m_cabecalhos,Formatter log) {
        matrizTotalCriterios = new double[N_CRITERIOS * N_ALTERNATIVAS][N_ALTERNATIVAS];
        int c = 0, j, k;
        m_cabecalhos = new String[N_CRITERIOS][N_ALTERNATIVAS + 1];
        for (j = 0; j < matrizTotalCriterios.length + matrizCriterios.length; j++) {
            if (matrizInputTotal[j + (matrizCriterios.length + 1)][0].contains("mc") != true) {
                for (k = 0; k < matrizTotalCriterios[c].length; k++) {
                    if (matrizInputTotal[j + (matrizCriterios.length + 1)][k].contains(".") || matrizInputTotal[j + (matrizCriterios.length + 1)][k].contains(",")) {
                        log.format("Formato errado (Posicao ("+(j+(matrizCriterios.length + 1)+1)+","+(k+1)+")): ",matrizInputTotal[j + 1][k]);
                        System.out.println("Erro detetado, verificar Log");
                        log.close();
                        System.exit(0);
                    } else {
                        matrizTotalCriterios[c][k] = StringToDouble(matrizInputTotal[j + (matrizCriterios.length + 1)][k]);
                    }
                }
                c++;
            }
        }
        return matrizTotalCriterios;
    }

    public static String[][] criarMatrizCabecalhos(String[][] matrizInputTotal, double[][] matrizCriterios, int N_CRITERIOS, int N_ALTERNATIVAS, int nLinhas, String[][] m_cabecalhos) {
        int p = 0, j, k;
        m_cabecalhos = new String[N_CRITERIOS + 1][N_ALTERNATIVAS + 1];
        for (j = 0; j < nLinhas; j++) {
            if (matrizInputTotal[j][0].contains("mc")) {
                for (k = 0; k < m_cabecalhos[p].length; k++) {
                    m_cabecalhos[p][k] = matrizInputTotal[j][k];
                }
                p++;
            }
        }
        return m_cabecalhos;
    }

    public static double StringToDouble(String N) {
        if (N.contains("/")) {
            String[] tempDiv = N.split("/");
            return Double.parseDouble(tempDiv[0]) / Double.parseDouble(tempDiv[1]);
        } else {
            return Double.parseDouble(N);
        }
    }

    public static double[][] criarMatrizSomatorios(double[][] matrizSomatorios, double[][] matrizCriterios, double[][] matrizTotalCriterios, int N_ALTERNATIVAS, int N_CRITERIOS) {
        matrizSomatorios = new double[N_CRITERIOS + 1][N_ALTERNATIVAS];
        matrizSomatorios[0] = somatoriosColunas(matrizCriterios);
        for (int i = 0; i < N_CRITERIOS; i++) {
            matrizSomatorios[i + 1] = somatoriosColunas(identificarMatriz(matrizTotalCriterios, i, N_ALTERNATIVAS));
        }
        return matrizSomatorios;
    }

    public static double[][] identificarMatriz(double[][] matrizTotalCriterios, int i, int N_ALTERNATIVAS) {
        double[][] matrizTemp = new double[N_ALTERNATIVAS][N_ALTERNATIVAS];
        for (int j = 0; j < matrizTemp.length; j++) {
            for (int k = 0; k < matrizTemp[j].length; k++) {
                matrizTemp[j][k] = matrizTotalCriterios[j + (i * N_ALTERNATIVAS)][k];
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

    public static double[][] normalizarMatrizes(double[][] matrizSomatorios, double[][] matrizTotalCriterios, double[][] matrizCriterios, double[][] matrizCriteriosNormalizada, double[][] matrizTotalNormalizacao, int N_CRITERIOS, int N_ALTERNATIVAS) {
        int nLinhasLidas = 0;
        matrizTotalNormalizacao = new double[matrizTotalCriterios.length][matrizTotalCriterios[0].length];
        for (int nMatriz = 0; nMatriz < N_CRITERIOS; nMatriz++) {
            double[][] matrizTemp = identificarMatriz(matrizTotalCriterios, nMatriz, N_ALTERNATIVAS);
            double[][] matrizNormalizadaTemp = normalizar(matrizSomatorios, matrizTemp, matrizTotalNormalizacao, nMatriz + 1);
            for (int i = 0; i < matrizTemp.length; i++) {
                for (int j = 0; j < matrizTemp[i].length; j++) {
                    matrizTotalNormalizacao[i + nLinhasLidas][j] = matrizNormalizadaTemp[i][j];
                }
            }
            nLinhasLidas = nLinhasLidas + matrizNormalizadaTemp.length;
        }
        return matrizTotalNormalizacao;
    }

    public static double[][] normalizar(double[][] matrizSomatorios, double[][] matriz, double[][] matrizNormalizada, int nMatriz) {
        matrizNormalizada = new double[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matrizNormalizada[j][i] = matriz[j][i] / matrizSomatorios[nMatriz][i];
            }
        }
        return matrizNormalizada;
    }

    public static double[][] prioridadeRelativa(double[][] mPrioridadeRelativa, double[][] matrizCriteriosNormalizada, double[][] matrizTotalNormalizacao, int N_CRITERIOS, int N_ALTERNATIVAS) {
        mPrioridadeRelativa = new double[N_CRITERIOS + 1][N_ALTERNATIVAS];
        for (int nMatriz = 0; nMatriz < N_CRITERIOS + 1; nMatriz++) {
            if (nMatriz == 0) {
                mPrioridadeRelativa = prioridadeLinhas(matrizCriteriosNormalizada, nMatriz, mPrioridadeRelativa);
            } else {
                mPrioridadeRelativa = prioridadeLinhas(identificarMatriz(matrizTotalNormalizacao, nMatriz - 1, N_ALTERNATIVAS), nMatriz, mPrioridadeRelativa);
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

    public static double[][] verificarConsistencia(int op, double[][] RCValues, double[][] mPrioridadeRelativa, double[][] matrizCriterios, double[][] matrizTotalCriterios) {
        if (op == 1) {
            RCValues = RCManualCheck(mPrioridadeRelativa, matrizCriterios, matrizTotalCriterios);
        } else if (op == 2) {
            RCValues = RCAutoCheck(mPrioridadeRelativa, matrizCriterios, matrizTotalCriterios);
        }
        System.out.println(" ");
        return RCValues;
    }

    public static double[][] RCManualCheck(double[][] mPrioridadeRelativa, double[][] matrizCriterios, double[][] matrizTotalCriterios) {
        double[][] cPrioridade;
        double[][] RCValues = new double[N_CRITERIOS + 1][3];//RCValues[][3], o 3 refere-se ao numero de colunas: RC,Valor Proprio,IR
        for (int nMatriz = 0; nMatriz < N_CRITERIOS + 1; nMatriz++) {
            if (nMatriz == 0) {
                cPrioridade = new double[N_CRITERIOS][1];
                cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                RCValues[nMatriz][0] = determinarManualRC(matrizCriterios, cPrioridade, nMatriz, RCValues);
            } else {
                cPrioridade = new double[N_ALTERNATIVAS][1];
                cPrioridade = encontrarColuna(mPrioridadeRelativa, cPrioridade, nMatriz);
                RCValues[nMatriz][0] = determinarManualRC(identificarMatriz(matrizTotalCriterios, nMatriz - 1, N_ALTERNATIVAS), cPrioridade, nMatriz, RCValues);
            }
        }
        return RCValues;
    }

    private static double[][] encontrarColuna(double[][] mPrioridadeRelativa, double[][] cPrioridade, int nMatriz) {
        if (nMatriz == 0) {
            for (int i = 0; i < mPrioridadeRelativa.length - 1; i++) {
                cPrioridade[i][0] = mPrioridadeRelativa[i][nMatriz];
            }
        } else if (nMatriz != 0) {
            for (int i = 0; i < mPrioridadeRelativa.length; i++) {
                cPrioridade[i][0] = mPrioridadeRelativa[i][nMatriz];
            }
        }
        return cPrioridade;
    }

    public static double determinarManualRC(double[][] matriz, double[][] cPrioridade, int nMatriz, double[][] RCValues) {
        double RC;
        double lambdaMax = 0;
        double IC, IR;
        lambdaMax = determinarLambdaMax(lambdaMax, matriz, cPrioridade, nMatriz);
        RCValues[nMatriz][1] = lambdaMax;
        IC = (lambdaMax - matriz.length) / (matriz.length - 1);
        IR = RandomConsistency[matriz.length - 1];
        RCValues[nMatriz][2] = IR;
        RC = IC / IR;
        return RC;
    }

    public static double determinarLambdaMax(double lambdaMax, double[][] matriz, double[][] cPrioridade, int nMatriz) {
        double[][] matrizTemp;
        matrizTemp = calcularMultiplicacao(matriz, cPrioridade);
        lambdaMax = calcularDiv(matrizTemp, cPrioridade, lambdaMax);
        return lambdaMax;
    }

    private static double[][] calcularMultiplicacao(double[][] matriz1, double[][] matriz2) {
        double[][] matrizTemp = new double[matriz1.length][matriz2[0].length];
        for (int i = 0; i < matriz1.length; i++) {
            for (int j = 0; j < matriz2[0].length; j++) {
                for (int k = 0; k < matriz1[i].length; k++) {
                    matrizTemp[i][j] += (matriz1[i][k] * matriz2[k][j]);
                }
            }
        }
        return matrizTemp;
    }

    private static double calcularDiv(double[][] matrizTemp, double[][] cPrioridade, double lambdaMax) {
        double somaTemp = 0;
        for (int i = 0; i < matrizTemp.length; i++) {
            somaTemp += matrizTemp[i][0] / cPrioridade[i][0];
        }
        lambdaMax = somaTemp / matrizTemp.length;
        return lambdaMax;
    }

    /**/
    public static double[][] RCAutoCheck(double[][] mPrioridadeRelativa, double[][] matrizCriterios, double[][] matrizTotalCriterios) {
        double[][] RCValues = new double[N_CRITERIOS + 1][3];
        Matrix matriz;
        double[][] matrizValores = null;
        for (int nMatriz = 0; nMatriz < N_CRITERIOS + 1; nMatriz++) {
            if (nMatriz == 0) {
                matriz = new Basic2DMatrix(matrizCriterios);
                matrizValores = decompor(matriz, matrizValores);
                RCValues[nMatriz][0] = determinarAutoRC(matrizCriterios, matrizValores, nMatriz, RCValues);
            } else {
                matriz = new Basic2DMatrix(identificarMatriz(matrizTotalCriterios, nMatriz - 1, N_ALTERNATIVAS));
                matrizValores = decompor(matriz, matrizValores);
                RCValues[nMatriz][0] = determinarAutoRC(identificarMatriz(matrizTotalCriterios, nMatriz - 1, N_ALTERNATIVAS), matrizValores, nMatriz, RCValues);
            }
        }
        return RCValues;
    }

    public static double[][] decompor(Matrix matriz, double[][] matrizValores) {
        EigenDecompositor eigenD = new EigenDecompositor(matriz);
        Matrix[] matrizDecomposta = eigenD.decompose();
        matrizValores = matrizDecomposta[1].toDenseMatrix().toArray();
        return matrizValores;
    }

    public static double determinarAutoRC(double[][] matriz, double[][] matrizValoresLambda, int nMatriz, double[][] RCValues) {
        double RC;
        double lambdaMax = 0;
        double IC, IR;
        lambdaMax = encontrarLambdaMax(matrizValoresLambda, lambdaMax);
        RCValues[nMatriz][1] = lambdaMax;
        IC = (lambdaMax - matriz.length) / (matriz.length - 1);
        IR = RandomConsistency[matriz.length - 1];
        RCValues[nMatriz][2] = IR;
        RC = IC / IR;
        return RC;
    }

    public static double encontrarLambdaMax(double[][] matrizValoresLambda, double lambdaMax) {
        double valorMaxTemp = matrizValoresLambda[0][0];
        for (int i = 0; i < matrizValoresLambda.length; i++) {
            for (int j = 0; j < matrizValoresLambda[i].length; j++) {
                if (matrizValoresLambda[i][j] > valorMaxTemp) {
                    valorMaxTemp = matrizValoresLambda[i][j];
                }
            }
        }
        lambdaMax = valorMaxTemp;
        return lambdaMax;
    }

    public static void selecaoOutput(String Output, int op, double[][] RCValues, double[][] matrizCriterios, String[][] m_cabecalhos, double[][] matrizTotalCriterios, double[][] matrizSomatorios, double[][] matrizCriteriosNormalizada, double[][] matrizTotalNormalizacao, double[][] mPrioridadeRelativa, double limiarRC, double limiarCriterio) throws FileNotFoundException {
        double escolhas[][];
        String[][] matrizTotal = new String[100][100];
        int nLinhasOutput = 0;
        nLinhasOutput = juntarDados(matrizTotal, nLinhasOutput, op, matrizCriterios, m_cabecalhos, matrizTotalCriterios, matrizSomatorios, matrizCriteriosNormalizada, matrizTotalNormalizacao, mPrioridadeRelativa);
        matrizTotal = eliminarNull(matrizTotal);
        printMatrizTotalInput(matrizTotal, nLinhasOutput);
        for (int i = 0; i < RCValues.length; i++) {
            if (RCValues[i][0] > limiarRC) {
                System.out.println("Os valores das prioridades relativas da " + (i + 1) + "ªmatriz inserida no input não são consistentes, RC:" + RCValues[i][0]);
            } else if (RCValues[i][0] <= 0.1) {
                System.out.println("Os valores das prioridades relativas da " + (i + 1) + "ªmatriz inserida no input são consistentes, RC:" + RCValues[i][0]);
            }
        }
        escolhas = calcularEscolhas(matrizCriterios, mPrioridadeRelativa, limiarCriterio, m_cabecalhos);
        System.out.println(" ");
        System.out.println("Escolhas:");
        printMatriz(escolhas);
        System.out.println("A melhor alternativa segundo os critérios é a alternativa " + encontrarMelhorEscolha(escolhas));
        guardarOutputTotalTXT(Output, matrizTotal, escolhas, nLinhasOutput, encontrarMelhorEscolha(escolhas), RCValues, mPrioridadeRelativa);
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

    private static int adicionarDadosCabecalho(String[][] matrizTotal, String[] matriz, int nLinhasOutput) {
        for (int i = 0; i < matriz.length; i++) {
            matrizTotal[nLinhasOutput][i] = matriz[i];
            matrizTotal[nLinhasOutput + i][0] = matriz[i];
        }
        nLinhasOutput++;
        return nLinhasOutput;
    }

    private static int adicionarDadosMatrizes(String[][] matrizTotal, String[][] matriz, int nLinhasOutput) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matrizTotal[nLinhasOutput][j + 1] = matriz[i][j];
            }
            nLinhasOutput++;
        }
        return nLinhasOutput;
    }

    private static int adicionarVetorPrioridades(String[][] matrizTotal, String[][] matrizPrioridades, int nLinhasOutput, int nColuna) {
        if (nColuna == 0) {
            for (int i = 0; i < matrizPrioridades.length; i++) {
                if (i == 0) {
                    matrizTotal[nLinhasOutput - matrizPrioridades[nColuna].length + i][N_ALTERNATIVAS + 1] = "Prioridade Relativa";
                } else {
                    matrizTotal[nLinhasOutput - matrizPrioridades[nColuna].length + i][N_ALTERNATIVAS + 1] = matrizPrioridades[i - 1][nColuna];
                }
            }
            nColuna++;
        } else if (nColuna != 0) {
            for (int i = -1; i < matrizPrioridades.length; i++) {
                if (i == -1) {
                    matrizTotal[nLinhasOutput - matrizPrioridades[nColuna].length + i][N_ALTERNATIVAS + 1] = "Prioridade Relativa";
                } else {
                    matrizTotal[nLinhasOutput - matrizPrioridades[nColuna].length + i][N_ALTERNATIVAS + 1] = matrizPrioridades[i][nColuna];
                }
            }
            nColuna++;
        }
        return nColuna;
    }

    public static String[][] DoubleToString(double[][] matriz, int op) {
        if (op == 1) {
            matriz = arredondar(matriz);
        }
        String[][] matrizConvert = new String[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matrizConvert[i][j] = String.valueOf(matriz[i][j]);
            }
        }
        return matrizConvert;
    }

    public static String[][] eliminarNull(String[][] matrizTotal) {
        for (int i = 0; i < matrizTotal.length; i++) {
            for (int j = 0; j < matrizTotal[i].length; j++) {
                if (matrizTotal[i][j] == null) {
                    matrizTotal[i][j] = " ";
                }
            }
        }
        return matrizTotal;
    }

    public static double[][] arredondar(double[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = (double) Math.round(matriz[i][j] * 100) / 100;
            }
        }
        return matriz;
    }

    private static void printMatrizTotalInput(String[][] matrizTotal, int nLinhasOutput) {
        for (int i = 0; i < nLinhasOutput / 2; i++) {
            for (int j = 0; j < matrizTotal[i].length; j++) {
                System.out.printf("%20s", matrizTotal[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }

    public static double[][] calcularEscolhas(double[][] matrizCriterios, double[][] mPrioridadeRelativa, double limiarCriterio, String[][] m_cabecalhos) {
        int[] posicao = new int[N_CRITERIOS];
        int contPos = 0, NCriteriosIrrelevantes = 0, c = 0;
        posicao = verificacaoLimiar(mPrioridadeRelativa, limiarCriterio, posicao);
        posicaoDadosIrrelevantes = new String[posicao.length];
        NCriteriosIrrelevantes = recontagemCriterios(NCriteriosIrrelevantes, posicao);
        double[][] matrizPrioridades = new double[matrizCriterios.length - NCriteriosIrrelevantes][1], escolhas;
        for (int i = 0; i < matrizPrioridades.length; i++) {
            if (posicao[i] != 1) {
                matrizPrioridades[contPos][0] = mPrioridadeRelativa[i][0];
                contPos++;
            }
        }
        System.out.println(" ");
        System.out.println("Os atributos eliminados são:");
        for (int k = 0; k < posicao.length; k++) {
            if (posicao[k] == 1) {
                c++;
                System.out.println(m_cabecalhos[0][k + 1]);
                posicaoDadosIrrelevantes[c] = m_cabecalhos[0][k + 1];
            }
        }
        contPos = 1;
        c = 0;
        double[][] matrizPrioridadesCriterios = new double[mPrioridadeRelativa.length][mPrioridadeRelativa.length - 1 - NCriteriosIrrelevantes];
        for (int i = 0; i < matrizPrioridadesCriterios.length; i++) {
            for (int j = 1; j < matrizPrioridadesCriterios[i].length + 1; j++) {
                c++;
                if (posicao[c - 1] != 1) {
                    matrizPrioridadesCriterios[i][contPos - 1] = mPrioridadeRelativa[i][j];
                    contPos++;
                }
            }
            contPos = 1;
            c = 0;
        }
        escolhas = calcularMultiplicacao(matrizPrioridadesCriterios, matrizPrioridades);
        return escolhas;
    }

    public static int[] verificacaoLimiar(double[][] mPrioridadeRelativa, double limiarCriterio, int[] posicao) {
        for (int i = 0; i < posicao.length; i++) {
            if (mPrioridadeRelativa[i][0] < limiarCriterio) {
                posicao[i] = 1;
            }
        }
        return posicao;
    }

    public static int recontagemCriterios(int NCriteriosIrrelevantes, int[] posicao) {
        for (int i = 0; i < posicao.length; i++) {
            if (posicao[i] != 0) {
                NCriteriosIrrelevantes++;
            }
        }
        return NCriteriosIrrelevantes;
    }

    public static int encontrarMelhorEscolha(double[][] escolhas) {
        int melhorEscolha = 1;
        double melhorPontuacao = escolhas[0][0];
        for (int i = 1; i < escolhas.length; i++) {
            if (escolhas[i][0] > melhorPontuacao) {
                melhorPontuacao = escolhas[i][0];
                melhorEscolha = i + 1;
            }
        }
        return melhorEscolha;
    }

    public static void guardarOutputTotalTXT(String Output, String[][] matrizTotal, double[][] escolhas, int nLinhasOutput, int melhorEscolha, double[][] RCValues, double[][] mPrioridadeRelativa) throws FileNotFoundException {
        Formatter out = new Formatter(new File(Output));
        for (int i = 0; i < nLinhasOutput; i++) {
            for (int j = 0; j < matrizTotal[i].length; j++) {
                out.format("%20s", matrizTotal[i][j]);
            }
            out.format("%n");
        }
        out.format("%25s", "Atributos Ignorados:");
        out.format("%n");
        for (int i = 0; i < posicaoDadosIrrelevantes.length; i++) {
            if (posicaoDadosIrrelevantes[i] != null) {
                out.format("%25s", posicaoDadosIrrelevantes[i]);
            }
        }
        out.format("%n");
        out.format("%25s", "RC");
        out.format("%25s", "Valor Próprio Máx");
        out.format("%25s", "IR");
        out.format("%n");
        for (int a = 0; a < RCValues.length; a++) {
            for (int b = 0; b < RCValues[a].length; b++) {
                out.format("%25s", (a + 1) + " : " + (double) Math.round(RCValues[a][b] * 1000) / 1000);
            }
            out.format("%n");
        }
        out.format("%n");
        out.format("%20s", "Prioridade Composta");
        out.format("%n");

        for (int a = 0; a < mPrioridadeRelativa.length; a++) {
            for (int b = 1; b < mPrioridadeRelativa[a].length; b++) {
                out.format("%20s", " | " + (double) Math.round(mPrioridadeRelativa[a][b] * 1000) / 1000);
            }
            out.format("%n");
        }
        out.format("%20s", "Pontuação Final");
        out.format("%n");
        for (int a = 0; a < escolhas.length; a++) {
            for (int b = 0; b < escolhas[a].length; b++) {
                out.format("%20s", "Alternativa " + (a + 1) + ": " + (double) Math.round(escolhas[a][b] * 100) / 100);
            }
            out.format("%n");
        }
        out.format("%n");
        out.format("A melhor alternativa é a alternativa: " + melhorEscolha);
        out.close();
    }
}
