package org.labgua.similarity.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.labgua.similarity.entity.IDocumentable;
import org.labgua.similarity.entity.SUtility;

public class TestData {

	public static class EntityTest implements IDocumentable{

		private static int CLASS_ID = 0;
		
		private int id;
		private String titolo;
		private String messaggio;
		private String indirizzo;
				
		public EntityTest(String titolo, String messaggio, String indirizzo) {
			this.id = CLASS_ID;
			this.titolo = titolo;
			this.messaggio = messaggio;
			this.indirizzo = indirizzo;
			CLASS_ID++;
		}

		@Override
		public Document toDocument() {
			
			List<String> content = new ArrayList<>();
			content.add(titolo);
			content.add(messaggio);
			content.add(indirizzo);
			
			return SUtility.createDoc(id, content);	
		}

		@Override
		public String toString() {
			return "EntityTest [id=" + id + ", titolo=" + titolo + ", messaggio=" + messaggio + ", indirizzo="
					+ indirizzo + "]";
		}

		@Override
		public Integer getId() {
			return this.id;
		}
		
		
		
	}
	
	public static List<EntityTest> getListData(){
		
		List<EntityTest> out = new ArrayList<>();
		
		out.add( 
				new EntityTest(
						"Stupri: Siorini espulso da NcS",
						"Lo afferma il coordinatore pugliese di Noi con Salvini (NcS), Rossano Sasso, dopo il post pubblicato su Fb da Siorini, coordinatore cittadino di NcS a San Giovanni Rotondo (Foggia), che si chiedeva quando sarebbe successo un episodio come gli stupri di Rimini ", 
						""
		));

		
		out.add( 
				new EntityTest(
						"Nube tossica Sussex,soccorse 150 persone",
						"Circa 150 persone sono state soccorse a causa della nube tossica di origine indeterminata che ieri ha colpito il litorale del Sussex, sud dell'Inghilterra. La polizia locale afferma che si tratta di problemi medici di lieve entità, fra cui irritazione agli occhi, mal di gola e nausea, sottolineando che l'allarme è rientrato.", 
						""
		));
		
		out.add( 
				new EntityTest(
						"Omaggi, crisi, maestri, i docu a Venezia",
						"Due i documentari nel concorso ufficiale: Human Flow di Ai Weiwei, nel quale l'artista cinese dissidente racconta migranti e rifugiati e l'affresco firmato da Frederick Wiseman 'Ex Libris - The New York Public Library' su una delle più grandi istituzioni del sapere del mondo", 
						""
		));
		
		out.add( 
				new EntityTest(
						"Harvey: Trump, stato emergenza Louisiana",
						"Il presidente Donald Trump ha dichiarato lo stato di emergenza in Louisiana e disposto l'assistenza federale in seguito al passaggio dell'uragano Harvey. Lo rende noto la Casa Bianca. Intanto il mondo delle grande aziende Usa Si mobilita per aiutare le persone colpite in Texas.", 
						""
		));
		
		
		
		/////sport
		out.add( 
				new EntityTest(
						"Azzurri: mattina di test fisico-atletici",
						"Mattinata di test fisico-atletici per gli azzurri di Gian Piero Ventura per valutare sforzo e recupero a 4 giorni dalla sfida con la Spagna a Madrid, per le qualificazioni ai Mondiali 2018. I giocatori (ad eccezione dei tre portieri), sono stati divisi in due gruppi da cinque e in tre da quattro: prima hanno effettuato tre test di corsa a diverse velocità al termine del quale sono stati valutati la frequenza cardiaca, la percezione dello sforzo e i valori del lattato; nel secondo test, i giocatori hanno compiuto una corsa a 18 km/h costanti ripetendola per 10 volte, per valutare i valori del lattato, i valori tampone e quelli di recupero.",
						""
		));
		out.add( 
				new EntityTest(
						"Roma: visite mediche in corso per Schick",
						"Giornata di visite mediche per il neo acquisto della Roma Patrik Schick. L'attaccante ceco, prelevato dalla Sampdoria e sbarcato ieri sera nella Capitale, è arrivato da poco presso la clinica romana Villa Stuart per sottoporsi agli esami di rito che anticipano la firma sul contratto. Il giocatore in mattinata ha fatto anche una breve tappa nel centro sportivo di Trigoria e conosciuto il tecnico Eusebio Di Francesco.",
						""
		));
		out.add( 
				new EntityTest(
						"Calcio: posticipo B, Bari-Cesena 3-0",
						"Esordio stagionale con brillante vittoria del Bari al San Nicola contro il Cesena nel posticipo della prima giornata della serie D. Davanti a quasi 18mila spettatori, l'undici allenato da Fabio Grosso (prima panchina tra i professionisti per il campione di Germania 2006), ha impressionato per la varietà delle soluzioni di gioco e la condizione atletica, passando in vantaggio con Improta al 38' su assist di Tello. Nella ripresa il raddoppio al 14' grazie ad un triangolo magistrale Improta-Nenè-Improta, con assist per Galano che ha battuto Fulignati.",
						""
		));
		out.add( 
				new EntityTest(
						"Van der Wiel,io a servizio della squadra",
						"Primo olandese della storia del Cagliari. Gregory Van der Wiel, classe 1988, ex Ajax e Paris Sain Germain, è pronto a indossare la maglia numero due e a fare il pendolino lungo la fascia destra. \"Sono felice di essere qui - queste le sue prime parole da rossoblù - ho ricevuto un caldo benvenuto da parte di tutti. Non ero mai stato in Sardegna prima, ora intendo restare il più a lungo possibile. Voglio tornare a divertirmi giocando a calcio\".",
						""
		));
		
		return out;
		
	}
	
}
