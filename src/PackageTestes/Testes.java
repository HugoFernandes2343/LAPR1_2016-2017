
package PackageTestes;
/**
 *
 * @author PC
 */
public class Testes {
    
    public static void main(String[] args){
            MétodoArredondar();
            StringToDouble();
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

}
