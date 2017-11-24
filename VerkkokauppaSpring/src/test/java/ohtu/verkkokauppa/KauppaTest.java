package ohtu.verkkokauppa;

import ohtu.verkkokauppa.Pankki;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class KauppaTest {

    Pankki pankki;
    Viitegeneraattori viite;
    Kauppa k;

    @Before
    public void setUp() {
        pankki = mock(Pankki.class);
        
        viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "limppu", 6));
        
        when(varasto.saldo(3)).thenReturn(0);
        when(varasto.haeTuote(3)).thenReturn(new Tuote(3, "limppu", 7));

        k = new Kauppa(varasto, pankki, viite);
    }

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");
        
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));
    }

    @Test
    public void ostetaanKaksiJaKutsutaanTilisiirtoa() {
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(11));
    }

    @Test
    public void ostetaanKaksiSamaaJaKutsutaanTilisiirtoa() {

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(10));
    }

    @Test
    public void ostetaanKaksiJoistaToinenLoppuJaKutsutaanTilisiirtoa() {

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(3);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));
    }

    @Test
    public void aloitaAsiointiTyhjentaaKorin() {
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(6));
    }
    
    @Test
    public void uusiViitenumeroJokaiselleMaksutapahtumalle() {
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);        
        k.tilimaksu("pekka", "12345");
        k.aloitaAsiointi();
        k.lisaaKoriin(1);        
        k.tilimaksu("pekka", "12345");

        verify(viite, times(2)).uusi();
    }
    
    

}
