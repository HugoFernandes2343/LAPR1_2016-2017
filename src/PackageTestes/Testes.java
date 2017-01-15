package PackageTestes;

import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;
import projetolapr1_iteracao2.MetodoAHP;
import projetolapr1_iteracao2.MetodoTOPSIS;

/**
 *
 * @author PC
 */
public class Testes {

    public static void main(String[] args) {
        MétodoArredondarTeste();
        System.out.println("");
        arredondar_teste();
        System.out.println("");
        StringToDouble_teste();
        System.out.println("");
        decomporMatriz_teste();
        System.out.println("");
        eliminarNulls_teste();
        System.out.println("");
        somatoriosColunas_teste();
        matrizNormalizada_teste();
        System.out.println("");
        operacaoSomatorio_teste();
        System.out.println("");
        matrizPesada_teste();
        System.out.println("");
        detDistanciaIdealP_teste();
        System.out.println("");
        detDistanciaIdealN_teste();
        System.out.println("");
    }

    /*Arredondar Valores com duas casas decimais de tolerância*/
    public static void MétodoArredondarTeste() {
        System.out.println("Arredondar 0.4999999999999 para 0.5");
        double a = 0.4999999999999;
        double b = (double) Math.round(a * 100) / 100;
        System.out.println(b);
    }

    /*Arredondar matriz*/
    public static void arredondar_teste() {
        double[][] matriz = {{1.5678, 2.4355}, {12.4567, 45.4556}};
        System.out.println("Arredondar (com duas casas decimais):");
        MetodoAHP.arredondar(matriz);
        System.out.println("");
        System.out.println("Resultado esperado : {1.57,2.44},{12.46,45.46}");
        System.out.println("");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%20s", matriz[i][j]);
            }
            System.out.println("");
        }
    }

    /*Converter Strings para doubles*/
    public static void StringToDouble_teste() {
        System.out.println("Tansformar 2/5 em 0.4");
        String valor = "2/5";
        System.out.println(MetodoAHP.StringToDouble(valor));
    }

    /*Testar Eigen Decomposition*/
    public static void decomporMatriz_teste() {
        System.out.println("Matriz : {1,2,3},{1,2,3},{1,2,3}");
        System.out.println("Resultado esperado (aproximadamente),Lambda Máximo = 6");
        double[][] matrizDados = {{1, 2, 3}, {1, 2, 3}, {1, 2, 3}};
        Matrix matriz = new Basic2DMatrix(matrizDados);
        double[][] matrizValores = MetodoAHP.decompor(matriz, matrizDados);
        for (int i = 0; i < matrizValores.length; i++) {
            for (int j = 0; j < matrizValores[i].length; j++) {
                System.out.printf("%20s", matrizValores[i][j]);
            }
            System.out.println("");
        }
    }

    /*Eliminar Nulls numa matriz*/
    public static void eliminarNulls_teste() {
        String[][] matriz = new String[2][2];
        System.out.println("Input:");
        System.out.println("");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%10s", "|" + matriz[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println("Todos os valores null transformam-se em espaços em branco");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j] == null) {
                    matriz[i][j] = "";
                }
            }
        }
        System.out.println("Output:");
        System.out.println("");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%10s", "|" + matriz[i][j]);
            }
            System.out.println(" ");
        }
    }

    /*Criar vetor dos somatorios para uma determinada matriz*/
    public static void somatoriosColunas_teste() {
        double[][] matriz = {{2, 4}, {4, 2}};
        System.out.println("Matriz:");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%20s", matriz[i][j]);
            }
            System.out.println("");
        }
        System.out.println("Resultado esperado: |6.0| |6.0| ");
        double[] somatorio = new double[matriz.length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                somatorio[i] = somatorio[i] + matriz[j][i];
            }
        }
        for (int i = 0; i < somatorio.length; i++) {
            System.out.printf("%10s", "|" + somatorio[i] + "|");
        }
    }

    /*normalizar a matriz inicial*/
    public static void matrizNormalizada_teste() {
        double[][] matriznormal = {{5, 6}, {7, 4}};
        double[][] matriznormalizadaesperada = {{0.58123, 0.83205}, {0.81373, 0.55470}};
        String[] crit = {"custo", "estilo"};
        String[] alt = {"marca1", "marca2"};

        System.out.println("");
        System.out.println("Matriz:");

        for (int i = 0; i < matriznormal.length; i++) {
            for (int j = 0; j < matriznormal[i].length; j++) {
                System.out.printf("%20s", matriznormal[i][j]);
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Matriz Esperada:");

        for (int i = 0; i < matriznormalizadaesperada.length; i++) {
            for (int j = 0; j < matriznormalizadaesperada[i].length; j++) {
                System.out.printf("%20s", matriznormalizadaesperada[i][j]);
            }
            System.out.println("");
        }
        System.out.println("Output");
        double[][] matriznormalizada = MetodoTOPSIS.matrizNormalizada(matriznormal, alt, crit);
        for (int i = 0; i < matriznormalizada.length; i++) {
            for (int j = 0; j < matriznormalizada[i].length; j++) {
                System.out.printf("%20s", matriznormalizada[i][j]);
            }
            System.out.println("");
        }
    }

    /*efetuar o somatorio das colunas na matriz normalizada*/
    public static void operacaoSomatorio_teste() {

    }

    /* depois de efetuar o somatorio multiplica-se a cada coluna pelo respetivo peso do criterio*/
    public static void matrizPesada_teste() {

    }

    /* utilizar a matriz pesada e efetuar a raiz do somatorio de cada linha para os valores ideais positivos*/
    public static void detDistanciaIdealP_teste() {

    }

    /* mesmo metodo que foi acima enunciado mas para os valores negativos*/
    public static void detDistanciaIdealN_teste() {

    }
}
