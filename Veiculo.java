public class Veiculo{
    private int ano;
    private String modelo;
    private String marca;
    private double velocidadeAtual;
    private double combustivel;
    private boolean motorLigado;

    public int getAno() {
        return ano;
    }
    public void setAno(int ano) {
        this.ano = ano;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public double getVelocidadeAtual() {
        return velocidadeAtual;
    }
    public void setVelocidadeAtual(double velocidadeAtual) {
        this.velocidadeAtual = velocidadeAtual;
    }
    public double getCombustivel() {
        return combustivel;
    }
    public void setCombustivel(double combustivel) {
        this.combustivel = combustivel;
    }
    public boolean isMotorLigado() {
        return motorLigado;
    }
    public void setMotorLigado(boolean motorLigado) {
        this.motorLigado = motorLigado;
    }

    public void ligarMotor() {
        if (!motorLigado) {
            motorLigado = true;
            System.out.println("Motor ligado.");
        } else {
            System.out.println("O motor já está ligado.");
        }
    }
    public void desligarMotor() {
        if (motorLigado) {
            motorLigado = false;
            System.out.println("Motor desligado.");
        } else {
            System.out.println("O motor já está desligado.");
        }
    }

    public void acelerar(double incremento) {
        if (motorLigado) {
            if (combustivel > 0) {
                velocidadeAtual += incremento;
                combustivel -= incremento * 0.1; // Consome combustível proporcionalmente à aceleração
                System.out.println("Acelerando. Velocidade atual: " + velocidadeAtual + " km/h. Combustível restante: " + combustivel + " litros.");
            } else {
                System.out.println("Combustível insuficiente para acelerar.");
            }
        } else {
            System.out.println("O motor está desligado. Ligue o motor para acelerar.");
        }
    }

    public void frear(double decremento) {
        if (motorLigado) {
            if (velocidadeAtual > 0) {
                velocidadeAtual -= decremento;
                if (velocidadeAtual < 0) {
                    velocidadeAtual = 0;
                }
                System.out.println("Freando. Velocidade atual: " + velocidadeAtual + " km/h.");
            } else {
                System.out.println("O veículo já está parado.");
            }
        } else {
            System.out.println("O motor está desligado. Ligue o motor para frear.");
        }
    }
    public void exibirInformacoes() {
        System.out.println("Marca: " + marca);
        System.out.println("Modelo: " + modelo);
        System.out.println("Ano: " + ano);
        System.out.println("Velocidade Atual: " + velocidadeAtual + " km/h");
        System.out.println("Combustível: " + combustivel + " litros");
        System.out.println("Motor Ligado: " + (motorLigado ? "Sim" : "Não"));
    }
}