public class dosen {
    private int NID;
    private int idUser;
    private String namaDosen;
    private String password;
    private int idProdi;

    public dosen(int NID, int idUser,String nd,String pw,int idProdi){
        this.NID = NID;
        this.idUser = idUser;
        this.namaDosen = nd;
        this.password = pw;
        this.idProdi =idProdi;
    }

     public int getNid() { 
        return NID; 
    }
    public void setNid(int nid) { 
        this.NID = nid; 
    }

    public int getIdUser() { 
        return idUser; 
    }
    public void setIdUser(int idUser) { 
        this.idUser = idUser; 
    }

    public String getNamaDosen() { 
        return namaDosen; 
    }
    public void setNamaDosen(String nd) { 
        this.namaDosen = nd; 
    }

    public String getPassword() { 
        return password; 
    }
    public void setPassword(String pw) { 
        this.password = pw; 
    }

    public int getIdProdi() { 
        return idProdi; 
    }
    public void setIdProdi(int idProdi) { 
        this.idProdi = idProdi;
     }
}
