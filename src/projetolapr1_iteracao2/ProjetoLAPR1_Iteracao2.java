package projetolapr1_iteracao2;

import java.io.FileNotFoundException;
import projetolapr1_iteracao2.MetodoAHP;
import projetolapr1_iteracao2.MetodoTOPSIS;

/**
 *
 * @author PC
 */
public class ProjetoLAPR1_Iteracao2 {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        String metodo = args[0];
        if (metodo.equals("-M1")) {
            if (args[1].contains("-L")) {
                if (args[2].contains("-S")) {
                    double limiarCriterio = lerLimiar(args[1]);
                    double limiarRC = lerLimiar(args[2]);
                    String Input = args[3];
                    String Output = args[4];
                    MetodoAHP.main(limiarCriterio, limiarRC, Input, Output);
                } else {
                    double limiarCriterio = lerLimiar(args[1]);
                    double limiarRC = 0.1;
                    String Input = args[2];
                    String Output = args[3];
                    MetodoAHP.main(limiarCriterio, limiarRC, Input, Output);
                }
                double limiarCriterio = lerLimiar(args[1]);
                double limiarRC = lerLimiar(args[2]);
                String Input = args[3];
                String Output = args[4];
                MetodoAHP.main(limiarCriterio, limiarRC, Input, Output);
            } else if (args[1].contains("-S")) {
                double limiarCriterio = 0;
                double limiarRC = lerLimiar(args[1]);
                String Input = args[2];
                String Output = args[3];
                MetodoAHP.main(limiarCriterio, limiarRC, Input, Output);
            } else {
                double limiarCriterio = 0;
                double limiarRC = 0.1;
                String Input = args[1];
                String Output = args[2];
                MetodoAHP.main(limiarCriterio, limiarRC, Input, Output);
            }
            /*}else if(metodo.equals("-M2")){
            String Input=args[1];
            String Output=args[2];
            MetodoTOPSIS.main(Input,Output);/*Só no final do code é que se implementa*/
        }
    }

    private static double lerLimiar(String limiar) {
        limiar = limiar.substring(2);
        return Double.parseDouble(limiar);
    }

}
