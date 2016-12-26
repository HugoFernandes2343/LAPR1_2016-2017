
package PackageTestes;

import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 *
 * @author PC
 */
public class Testes {
    
    public static void main(String[] args){
            MétodoArredondar();
            StringToDouble();
            decomporMatriz();
            eliminarNulls();
    }
    /*Arredondar Valores com duas casas decimais de tolerância*/
    public static void MétodoArredondar(){
        System.out.println("Arredondar 0.4999999999999 para 0.5");
            double a = 0.4999999999999;
            double b = (double) Math.round(a * 100) / 100;
            System.out.println(b);
    }
    /*Converter Strings para doubles*/
    public static void StringToDouble(){
        System.out.println("Tansformar 2/5 em 0.4");
        String valor="2/5";
        if (valor.contains("/")) {
            String[] tempDiv = valor.split("/");
            System.out.println(Double.parseDouble(tempDiv[0]) / Double.parseDouble(tempDiv[1]));
        }
    }
    
    /*Testar Eigen Decomposition*/
    public static void decomporMatriz(){
        System.out.println("Matriz : {1,2,3},{1,2,3},{1,2,3}");
        System.out.println("Resultado esperado,Lambda Máximo = 6");
        double[][] matrizValores;
        double[][] matrizDados={{1,2,3},{1,2,3},{1,2,3}};
        Matrix matriz =new Basic2DMatrix(matrizDados);
        EigenDecompositor eigenD=new EigenDecompositor(matriz);
        Matrix[] matrizDecomposta=eigenD.decompose();
        matrizValores=matrizDecomposta[1].toDenseMatrix().toArray();
        for(int i=0;i<matrizValores.length;i++){
            for(int j=0;j<matrizValores[i].length;j++){
                System.out.printf("%20s",matrizValores[i][j]);
            }
            System.out.println("");
        }
    }
    
    /*Eliminar Nulls numa matriz*/
    public static void eliminarNulls(){
        String[][] matriz=new String[2][2];
        System.out.println("Input:");
        System.out.println("");
        for(int i=0;i<matriz.length;i++){
            for(int j=0;j<matriz[i].length;j++){
                System.out.printf("%10s","|"+ matriz[i][j]);
            }
            System.out.println(" ");
        }    
        System.out.println("Todos os valores null transformam-se em espaços em branco");
        for(int i=0;i<matriz.length;i++){
            for(int j=0;j<matriz[i].length;j++){
                if(matriz[i][j]==null){
                    matriz[i][j]="";
                }
            }
        }
        System.out.println("Output:");
        System.out.println("");
        for(int i=0;i<matriz.length;i++){
            for(int j=0;j<matriz[i].length;j++){
                System.out.printf("%10s", "|"+ matriz[i][j]);
            }
            System.out.println(" ");
        }  
    }
}
