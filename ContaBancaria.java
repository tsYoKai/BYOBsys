public class ContaBancaria  {

    private double saldo;  


    public void depositar(double valor){
        if (valor > 0){
        saldo = saldo + valor;
        }
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getSaldo() {
        return saldo;
    }
}
