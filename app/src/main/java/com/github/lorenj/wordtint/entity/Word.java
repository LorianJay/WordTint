package com.github.lorenj.wordtint.entity;

public class Word {

    private int WordID;
    private String WordOrigin;
    private String WordPhonetics;
    private String category;
    private int categoryID;

    private String ADJ;
    private String ADV;
    private String V;
    private String VI;
    private String VT;
    private String N;
    private String CONJ;
    private String PRON;
    private String NUM;
    private String ART;
    private String PREP;
    private String INT;
    private String AUX;
    private String ExampleSentence;
    private String Phrase;
    private String Distinguish;

    public Word() {
    }

    public Word(int wordID, String wordOrigin, String wordPhonetics, String category, int categoryID, String ADJ, String ADV, String v, String VI, String VT, String n, String CONJ, String PRON, String NUM, String ART, String PREP, String INT, String AUX, String exampleSentence, String phrase, String distinguish) {
        WordID = wordID;
        WordOrigin = wordOrigin;
        WordPhonetics = wordPhonetics;
        this.category = category;
        this.categoryID = categoryID;
        this.ADJ = ADJ;
        this.ADV = ADV;
        V = v;
        this.VI = VI;
        this.VT = VT;
        N = n;
        this.CONJ = CONJ;
        this.PRON = PRON;
        this.NUM = NUM;
        this.ART = ART;
        this.PREP = PREP;
        this.INT = INT;
        this.AUX = AUX;
        ExampleSentence = exampleSentence;
        Phrase = phrase;
        Distinguish = distinguish;
    }

    public int getWordID() {
        return WordID;
    }

    public void setWordID(int wordID) {
        WordID = wordID;
    }

    public String getWordOrigin() {
        return WordOrigin;
    }

    public void setWordOrigin(String wordOrigin) {
        WordOrigin = wordOrigin;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getADJ() {
        return ADJ;
    }

    public void setADJ(String ADJ) {
        this.ADJ = ADJ;
    }

    public String getADV() {
        return ADV;
    }

    public void setADV(String ADV) {
        this.ADV = ADV;
    }

    public String getV() {
        return V;
    }

    public void setV(String v) {
        V = v;
    }

    public String getVI() {
        return VI;
    }

    public void setVI(String VI) {
        this.VI = VI;
    }

    public String getVT() {
        return VT;
    }

    public void setVT(String VT) {
        this.VT = VT;
    }

    public String getN() {
        return N;
    }

    public String getWordPhonetics() {
        return WordPhonetics;
    }

    public void setWordPhonetics(String wordPhonetics) {
        WordPhonetics = wordPhonetics;
    }

    public void setN(String n) {
        N = n;
    }

    public String getCONJ() {
        return CONJ;
    }

    public void setCONJ(String CONJ) {
        this.CONJ = CONJ;
    }

    public String getPRON() {
        return PRON;
    }

    public void setPRON(String PRON) {
        this.PRON = PRON;
    }

    public String getNUM() {
        return NUM;
    }

    public void setNUM(String NUM) {
        this.NUM = NUM;
    }

    public String getART() {
        return ART;
    }

    public void setART(String ART) {
        this.ART = ART;
    }

    public String getPREP() {
        return PREP;
    }

    public void setPREP(String PREP) {
        this.PREP = PREP;
    }

    public String getINT() {
        return INT;
    }

    public void setINT(String INT) {
        this.INT = INT;
    }

    public String getAUX() {
        return AUX;
    }

    public void setAUX(String AUX) {
        this.AUX = AUX;
    }

    public String getExampleSentence() {
        return ExampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        ExampleSentence = exampleSentence;
    }

    public String getPhrase() {
        return Phrase;
    }

    public void setPhrase(String phrase) {
        Phrase = phrase;
    }

    public String getDistinguish() {
        return Distinguish;
    }

    public void setDistinguish(String distinguish) {
        Distinguish = distinguish;
    }

    @Override
    public String toString() {
        return "Word{" +
                "WordID=" + WordID +
                ", WordOrigin='" + WordOrigin + '\'' +
                ", category='" + category + '\'' +
                ", categoryID=" + categoryID +
                ", ADJ='" + ADJ + '\'' +
                ", ADV='" + ADV + '\'' +
                ", V='" + V + '\'' +
                ", VI='" + VI + '\'' +
                ", VT='" + VT + '\'' +
                ", N='" + N + '\'' +
                ", CONJ='" + CONJ + '\'' +
                ", PRON='" + PRON + '\'' +
                ", NUM='" + NUM + '\'' +
                ", ART='" + ART + '\'' +
                ", PREP='" + PREP + '\'' +
                ", INT='" + INT + '\'' +
                ", AUX='" + AUX + '\'' +
                ", ExampleSentence='" + ExampleSentence + '\'' +
                ", Phrase='" + Phrase + '\'' +
                ", Distinguish='" + Distinguish + '\'' +
                '}';
    }


}
