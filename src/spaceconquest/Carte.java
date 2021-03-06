/*
 * Gestion de la carte
 */
package spaceconquest;

import java.util.HashMap;   
import spaceconquest.Map.Case;
import spaceconquest.Map.Couleur;
import spaceconquest.Map.Couple;
import spaceconquest.ObjetCeleste.ObjetCeleste;
import spaceconquest.Race.Vaisseau;

/**
 *
 * @author simonetma
 */


public class Carte {
    private int taille;                                                         //nombre de "colonne" de la map, (la map a 3 fois plus de lignes que de colonnes)
    private HashMap<Couple,Case> cases;                                         //listes des cases
    private Couple caseSelectionnee;                                            //case actuellement sélectionnée par le joueur
    private Graphe graphe;
    //Constructeur
    public Carte(int _taille) {
        this.taille = _taille;
        this.cases = new HashMap<>(); 
        //initialisation de la map vide
        for(int i=1;i<= 3*_taille;i++) {
            for(int j=1;j<=_taille;j++) {
                this.cases.put(new Couple(i,j), new Case());
            }
        }
        this.graphe = new Graphe(cases.size());//Creation du graphe de la carte
        this.caseSelectionnee = null;
    }
    
    
    //getteur de la taille de la map
    public int getTaille() {
        return this.taille;
    }
    
    //getteur de la case en position i,j
    public Case getCase(int i,int j) {
        return this.cases.get(new Couple(i,j));
    }
    
    //getteur de la case en position c (couple)
    public Case getCase(Couple c) {
        return this.cases.get(c);
    }
    
    //ajoute un objet celeste (étoile, astéroide...) à la position i,j (Passer par la classe partie !)
    public void addObjetCeleste(ObjetCeleste obj, int i,int j) {
        this.getCase(i, j).addObjetCeleste(obj);
        if(obj != null) {
            obj.setPosition(new Couple(i,j));
        }
    }
    
    //ajoute un vaisseau à la position i,j (Passer par la classe partie !)
    public void addVaisseau(Vaisseau v,int i,int j) {
        this.getCase(i,j).addVaisseau(v);
        if(v !=null) {
            v.setPosition(new Couple(i,j));
        }
    }
    
    //fait bouger le vaisseau présent en case départ à la case arrivée (détruisant tout vaisseau présent à cette case)
    public void BougerVaisseau(Couple depart, Couple arrivee) {
        if(this.getCase(depart).getVaisseau() == null) {
            System.err.println("ERREUR : Aucun vaisseau en case "+depart);
            System.exit(0);
        }
        if(this.getCase(arrivee).getVaisseau() != null) {
            System.out.println("Le "+this.getCase(arrivee).getVaisseau() + " a été détruit !");
            this.getCase(arrivee).getVaisseau().setPosition(null);
        }
        this.getCase(arrivee).addVaisseau(this.getCase(depart).getVaisseau());
        this.getCase(depart).addVaisseau(null);
    }
    
    //méthode gérant ce qu'il se passe quand on clique sur une case en mode manuel
    public void selectionCase(Couple c) {
        if(c.equals(this.caseSelectionnee)) {
            //deselection de la case
            this.getCase(c).setCouleur(Couleur.Blanc);
            this.caseSelectionnee = null;
        }
        else {
            //si une case avait déja été sélectionnée
            if(this.caseSelectionnee != null) {
                //ajouter des conditions de déplacement
                //on fait bouger le vaisseau
                this.BougerVaisseau(this.caseSelectionnee, c);
                //on déselectionne la case
                this.getCase(this.caseSelectionnee).setCouleur(Couleur.Blanc);
                this.caseSelectionnee = null;
                //on passe le tour
                SpaceConquest.tourSuivant();
            }
            else {
                //si aucune case n'avait été selectionné
                //on vérifie que la case nouvellement sélectionné contient un vaisseau du joueur en cours
                if(this.getCase(c).getVaisseau() != null) {
                    if(this.getCase(c).getVaisseau().getRace() == SpaceConquest.getTour()) {
                        //on selectionne la case
                        this.getCase(c).setCouleur(Couleur.Rouge);
                        this.caseSelectionnee = c;
                    }
                }
            }
        }
    }

    public Graphe getGraphe() {return graphe;}
    
    /**
     * 
     * @return le graphe modélisant la carte
     */
    public Graphe getGrapheGrille(){
        int n = this.getTaille();
        for(int i = 1; i <= 3*n; i++){
            for(int j = 1; j <= n; j++){
                if(n%2 == 0){ //si la taille de la grille est paire
                    if((i == 3*n-1) && (j == n)){ //la fichtre case du coin en bas à droite relié à une seule case
                        this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                    }
                    else {
                        if((j == 1) && (i%2 == 0) && (i != 3*n)){ //relier 2 cases + case à gauche (que des lignes paires) et sauf le coin bas gauche
                            this.graphe.modifierMatrice(n*(i-1)+j,n*i+j , 1);
                            this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                        }
                        else { 
                            if((j == n) && (i%2 == 1)){//relier 2 cases + case à droite (sans la case du coin bas droit 
                                this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                                this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                            }
                            else{
                                if(i == 3*n-1){ //relier 2 cases + cases du bas + cases sur ligne impaire
                                    this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                                    this.graphe.modifierMatrice(n*(i-1)+j, n*i+j+1, 1);
                                }
                                else{
                                    if((i%2 == 0) && (i != 3*n)){ //relier 3 cases + ligne paire
                                        this.graphe.modifierMatrice(n*(i-1)+j, n*i+j-1, 1);
                                        this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                                        this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                                    }
                                    else{
                                        if(i%2 == 1){ //relier 3 cases + ligne impaire
                                            this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                                            this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                                            this.graphe.modifierMatrice(n*(i-1)+j, n*i+j+1, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else { //si elle est impaire
                    if((i == 3*n-1) && (j == 1)){ //la fichtre case du coin en bas à gauche relié à une seule case
                        this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                    }
                    else {
                        if((j == 1) && (i%2 == 0)){ //relier 2 cases + case à gauche (que des lignes paires) et sauf le coin bas gauche
                            this.graphe.modifierMatrice(n*(i-1)+j,n*i+j , 1);
                            this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                        }
                        else { 
                            if((j == n) && (i%2 == 1) && (i != 3*n)){//relier 2 cases + case à droite (sans la case du coin bas droit déja traitée)
                                this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                                this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                            }
                            else{
                                if(i == 3*n-1){ //relier 2 cases + cases du bas + cases sur ligne impaire
                                    this.graphe.modifierMatrice(n*(i-1)+j, n*i+j-1, 1);
                                    this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                                }
                                else{
                                    if((i%2 == 0) && (i != 3*n)){ //relier 3 cases + ligne paire
                                        this.graphe.modifierMatrice(n*(i-1)+j, n*i+j-1, 1);
                                        this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                                        this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                                    }
                                    else{
                                        if((i%2 == 1) && (i != 3*n)){ //relier 3 cases + ligne impaire
                                            this.graphe.modifierMatrice(n*(i-1)+j, n*i+j, 1);
                                            this.graphe.modifierMatrice(n*(i-1)+j, n*(i+1)+j, 1);
                                            this.graphe.modifierMatrice(n*(i-1)+j, n*i+j+1, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return this.graphe;        
    }
    
    public Graphe getGrapheZombie(){
        int n = this.taille;
        Graphe zombGraphe = this.getGrapheGrille();
       
        //parcours du graphe pour detecter où sont les étoiles
        for(int i = 1; i <= 3*n; i++){
            for(int j = 1; j <= n; j++){
                if(this.getCase(i, j).getObjetCeleste() != null){
                    if(this.getCase(i, j).getObjetCeleste().getType() == "etoile"){
                        zombGraphe.isolerSommet(n*(i-1)+j);
                    }
                }
            }
        }
        return zombGraphe;
    }
    
    /**
     * 
     * @return le graphe arrangé des licornes (valeur des cases arrangés sur les cases contenant un asteroides)
     */
    public Graphe getGrapheLicorne(){
        int n = this.taille;
        Graphe licoGraphe = this.getGrapheGrille();
        //idem que dans zombGraphe cette fois pour les astéroïdes
        for(int i = 1; i <= 3*n; i++){
            for(int j = 1; j <= n; j++){
                if(this.getCase(i, j).getObjetCeleste() != null){
                    if(this.getCase(i, j).getObjetCeleste().getType() == "asteroide"){
                        licoGraphe.modifierMatrice(i, j, 2);//2 pa necessaires pour passer sur ce sommet
                    }
                }
            }
        }
        return licoGraphe;
    }
    
    /**
     * Colore toute les cases de al cartes en blanc
     */
    public void effacerColoration(){
        int n = this.taille;
        for(int i = 1; i <= 3*n; i++){
            for(int j = 1; j <= n; j++){
                this.getCase(i, j).setCouleur(Couleur.Blanc);
             }
        }
    }
    /**
     * 
     * @param nSommet num du sommet à transformer
     * @param tailleGrille taille de la grille considéré (nombre de ligne ou de colonne)
     * @return c le couple correspondant
     */
    public Couple sommetToCouple(int nSommet, int tailleGrille){
        int i,j;
        i = nSommet/tailleGrille;
        if(nSommet%tailleGrille != 0) i+=1; //si on est passé à la ligne suivante
        j = nSommet%tailleGrille;
        if(j == 0) j = tailleGrille;
        Couple c = new Couple(i,j);
        return c;
    }
    
    public void coloreCase(int i, int j){
        this.getCase(i, j).setCouleur(Couleur.Vert);
    }
    
    public void colorationMouvement(int i, int j, Graphe g){
        int n = this.taille;
        int sommet = n*(i-1)+j;
        //parcours de la ligne pour trouver les voisins
        for(int k = 1; k <= 3*n*n; k++){
            if(g.getMatrice(sommet, k) == 1){
                //System.out.println("j'ai trouvé un coeff egal à 1");
                Couple voisin = this.sommetToCouple(k, this.taille);
                //System.out.println("je colore la case " + voisin);
                this.coloreCase(voisin.getX(), voisin.getY());
            }
        }
    }
}
