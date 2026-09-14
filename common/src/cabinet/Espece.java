package cabinet;

public class Espece implements java.io.Serializable
{
    private String nom;
    private int dureeVie;

    public Espece() {}

    public Espece(String nom, int dureeVie) 
    {
        this.nom = nom;
        this.dureeVie = dureeVie;
    }

    // Getters
    public String getNom() 
    {
        return this.nom;
    }

    public int getDureeVie() 
    {
        return this.dureeVie;
    }

    // Setters
    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public void setDureeVie(int dureeVie) 
    {
        this.dureeVie = dureeVie;
    }
}
