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
        double[][] matrizNormal = {{5, 6}, {7, 4}};
        double[][] matrizNormalizadaEsperada = {{0.58123, 0.83205}, {0.81373, 0.55470}};
        String[] crit = {"custo", "estilo"};
        String[] alt = {"marca1", "marca2"};

        System.out.println("");
        System.out.println("Matriz:");

        for (int i = 0; i < matrizNormal.length; i++) {
            for (int j = 0; j < matrizNormal[i].length; j++) {
                System.out.printf("%20s", matrizNormal[i][j]);
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Matriz Esperada:");

        for (int i = 0; i < matrizNormalizadaEsperada.length; i++) {
            for (int j = 0; j < matrizNormalizadaEsperada[i].length; j++) {
                System.out.printf("%20s", matrizNormalizadaEsperada[i][j]);
            }
            System.out.println("");
        }
        System.out.println("Output");
        double[][] matriznormalizada = MetodoTOPSIS.matrizNormalizada(matrizNormal, alt, crit);
        for (int i = 0; i < matriznormalizada.length; i++) {
            for (int j = 0; j < matriznormalizada[i].length; j++) {
                System.out.printf("%20s", matriznormalizada[i][j]);
            }
            System.out.println("");
        }
    }


    /* depois de efetuar o somatorio multiplica-se a cada coluna pelo respetivo peso do criterio*/
    public static void matrizPesada_teste() {
        double[][] matrizNormalizada = {{0.58123, 0.83205}, {0.81373, 0.55470}};
        double[] vecPesos = {0.4, 0.6};
        double[][] matrizNormalizadaPesadaEsperada = {{0.232492, 0.49923}, {0.325492, 0.33282}};

        System.out.println("");
        System.out.println("Vetor pesos:");
        for (int i = 0; i < vecPesos.length; i++) {
            System.out.printf("%20s", vecPesos[i]);
            System.out.println("");
        }

        System.out.println("");
        System.out.println("Matriz:");
        for (int i = 0; i < matrizNormalizada.length; i++) {
            for (int j = 0; j < matrizNormalizada[i].length; j++) {
                System.out.printf("%20s", matrizNormalizada[i][j]);
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Matriz Esperada:");

        for (int i = 0; i < matrizNormalizadaPesadaEsperada.length; i++) {
            for (int j = 0; j < matrizNormalizadaPesadaEsperada[i].length; j++) {
                System.out.printf("%20s", matrizNormalizadaPesadaEsperada[i][j]);
            }
            System.out.println("");
        }
        System.out.println("Output");
        double[][] matrizNormalizadaPesada = MetodoTOPSIS.matrizPesada(matrizNormalizada, vecPesos);
        for (int i = 0; i < matrizNormalizadaPesada.length; i++) {
            for (int j = 0; j < matrizNormalizadaPesada[i].length; j++) {
                System.out.printf("%20s", matrizNormalizadaPesada[i][j]);
            }
            System.out.println("");
        }
    }

    /* utilizar a matriz pesada e efetuar a raiz do somatorio de cada linha para os valores ideais positivos*/
    public static void detDistanciaIdealP_teste() {
        double[][] matrizNormalizadaPesada = {{0.232492, 0.49923}, {0.325492, 0.33282}};
        double[][] matrizValoresIdeais = {{0.325492, 0.49923}, {0.232492, 0.33282}};
        String[] crit = {"custo", "estilo"};
        String[] alt = {"marca1", "marca2"};
        double[] vetorDistanciaIdealEsperado = {0.093, 0.166};
        System.out.println("");
        System.out.println("Matriz Pesada:");
        for (int i = 0; i < matrizNormalizadaPesada.length; i++) {
            for (int j = 0; j < matrizNormalizadaPesada[i].length; j++) {
                System.out.printf("%20s", matrizNormalizadaPesada[i][j]);
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Valores ideais:");
        for (int i = 0; i < matrizNormalizadaPesada.length; i++) {
            System.out.printf("%20s", matrizValoresIdeais[0][i]);
        }

        System.out.println("");
        System.out.println("Distancia esperada:");
        for (int i = 0; i < vetorDistanciaIdealEsperado.length; i++) {
            System.out.printf("%20s", vetorDistanciaIdealEsperado[i]);
        }
        System.out.println("");
        System.out.println("Output");
        double[] vetorDistanciaIdeal = MetodoTOPSIS.detDistanciaIdealP(matrizNormalizadaPesada, alt, crit, matrizValoresIdeais);
        for (int i = 0; i < vetorDistanciaIdeal.length; i++) {
            System.out.printf("%20s", vetorDistanciaIdeal[i]);
        }
    }

    /* mesmo metodo que foi acima enunciado mas para os valores negativos*/
    public static void detDistanciaIdealN_teste() {
        double[][] matrizNormalizadaPesada = {{0.232492, 0.49923}, {0.325492, 0.33282}};
        double[][] matrizValoresIdeais = {{0.325492, 0.49923}, {0.232492, 0.33282}};
        String[] crit = {"custo", "estilo"};
        String[] alt = {"marca1", "marca2"};
        double[] vetorDistanciaIdealEsperado = {0.166, 0.093};
        System.out.println("");
        System.out.println("Matriz Pesada:");
        for (int i = 0; i < matrizNormalizadaPesada.length; i++) {
            for (int j = 0; j < matrizNormalizadaPesada[i].length; j++) {
                System.out.printf("%20s", matrizNormalizadaPesada[i][j]);
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Valores ideais negativos:");
        for (int i = 0; i < matrizNormalizadaPesada.length; i++) {
            System.out.printf("%20s", matrizValoresIdeais[1][i]);
        }

        System.out.println("");
        System.out.println("Distancia esperada:");
        for (int i = 0; i < vetorDistanciaIdealEsperado.length; i++) {
            System.out.printf("%20s", vetorDistanciaIdealEsperado[i]);
        }
        System.out.println("");
        System.out.println("Output");
        double[] vetorDistanciaIdealN = MetodoTOPSIS.detDistanciaIdealN(matrizNormalizadaPesada, alt, crit, matrizValoresIdeais);
        for (int i = 0; i < vetorDistanciaIdealN.length; i++) {
            System.out.printf("%20s", vetorDistanciaIdealN[i]);
        }
    }
}
