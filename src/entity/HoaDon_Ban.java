package entity;

public class HoaDon_Ban {

    private HoaDon hoaDon;

    private Ban ban;

    public HoaDon_Ban() {
    }

    public HoaDon_Ban(
            HoaDon hoaDon,
            Ban ban
    ) {

        this.hoaDon = hoaDon;

        this.ban = ban;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

   
}