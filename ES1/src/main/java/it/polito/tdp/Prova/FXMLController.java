package it.polito.tdp.Prova;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class FXMLController
{
	private static final int NMAX_E = 10; 
	private static final int TMAX_E = 4; 
	private static final int NMAX_N = 100; 
	private static final int TMAX_N = 8; 
	private static final int NMAX_H = 1000; 
	private static final int TMAX_H = 10;
	 //assegnati a seonda della diff
	private int NMAX;
	private int TMAX;
	//
	private enum Difficolta
	{
		EASY, NORMAL, HARD
	}
	private Difficolta difficolta;
	private Integer numSegreto;
	private Integer tentativiRimasti;
	private LinkedList<Integer> listaNumInseriti; 
	private String prove;
	
	private int top; // per aggiornare intervallo di aiuto
	private int least;
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private Label txtTentativiRimasti;
	@FXML
	private Label txtTentativiTot;
	@FXML
	private ProgressBar pgbBarra;
	@FXML
	private TextField txtInput;
	@FXML
	private Button btnProva;
	@FXML
	private TextArea txtAreaResult;
	@FXML
	private Label txtDifficoltaSelezionata;
	@FXML
	private Label txtNumeriProvati;
	@FXML
	private Button btnNuovaPartita;
	@FXML
	private HBox hboxSelectDifficulty;
	@FXML
	private TextArea txtAreaAssistita;

	@FXML
	void doAssistita(ActionEvent event)
	{
		// mostra/nasconde area di testo con aiuti
		if(txtAreaAssistita.isVisible())
			txtAreaAssistita.setVisible(false);
		else txtAreaAssistita.setVisible(true);
		
		this.aggiornaTestoAssistenza();
	}
	void aggiornaTestoAssistenza()
	{
		txtAreaAssistita.setText("intervallo in cui si trova il numero segreto: " + this.least + "-" + this.top);
		txtAreaAssistita.appendText("\nNumero suggerito per procedere: " + (int)(this.least+this.top)/2);
	}
	@FXML
	void setEasy(ActionEvent event)
	{
		selectDifficulty(Difficolta.EASY);
	}
	@FXML
	void setNormal(ActionEvent event)
	{
		selectDifficulty(Difficolta.NORMAL);
	}
	@FXML
	void setHard(ActionEvent event)
	{
		selectDifficulty(Difficolta.HARD);
	} 
	void selectDifficulty(Difficolta d)
	{
		//inizializzazioni new game 
		this.difficolta = d;
		switch (d)
		{
			case EASY:
				NMAX = NMAX_E;
				TMAX = TMAX_E;
				break;
			case NORMAL:
				NMAX = NMAX_N;
				TMAX = TMAX_N;
				break;
			case HARD:
				NMAX = NMAX_H;
				TMAX = TMAX_H;
				break;
		}
		//reset variabili
		this.numSegreto = (int) (Math.random() * NMAX + 1);
		System.out.println(this.numSegreto);
		this.listaNumInseriti = new LinkedList<>();
		this.top = NMAX;
		this.least = 1;
		//GFX
		this.attivaInput();
		this.btnNuovaPartita.setDisable(false);
		
		this.hboxSelectDifficulty.setVisible(false);
	
		this.txtDifficoltaSelezionata.setText("" + this.difficolta);

		this.resetGFX();
		
		this.aggiornaTestoAssistenza();
	}
	private void disattivaInput()
	{
		this.txtInput.setDisable(true);
		this.btnProva.setDisable(true);
	}
	private void attivaInput()
	{
		this.txtInput.setDisable(false);
		this.btnProva.setDisable(false);
	}
	private void resetGFX()
	{
		this.tentativiRimasti = TMAX;
		this.txtTentativiRimasti.setText("" + TMAX);
		this.txtTentativiTot.setText("" + TMAX);
		this.pgbBarra.setProgress(1);
		
		this.txtAreaResult.setText("");
		this.prove = "";
		this.txtNumeriProvati.setText(prove);
	}
	@FXML
	void doNuovaPartita(ActionEvent event)
	{
		//difficolta
		this.btnNuovaPartita.setDisable(true);
		this.hboxSelectDifficulty.setVisible(true);
		this.disattivaInput();
	}
	@FXML
	void doProva(ActionEvent event)
	{
		Integer numInput;
		try
		{
			numInput = Integer.parseInt(txtInput.getText());
			if (!this.listaNumInseriti.contains(numInput) && numInput >= 1 && numInput <= NMAX)
			{
				this.listaNumInseriti.add(numInput);
				this.prove += this.listaNumInseriti.size() == 1 ? this.listaNumInseriti.getLast() : ", " + this.listaNumInseriti.getLast();
				this.txtNumeriProvati.setText(prove);

				this.tentativiRimasti--;
				this.txtTentativiRimasti.setText("" + this.tentativiRimasti);
				this.pgbBarra.setProgress((double) this.tentativiRimasti / TMAX);

				if (numInput < this.numSegreto)
				{
					this.txtAreaResult.setText("TROPPO BASSO!");
					
					if(numInput > this.least)
						this.least = numInput + 1;
					this.aggiornaTestoAssistenza();
				}
				else if (numInput > this.numSegreto)
				{
					this.txtAreaResult.setText("TROPPO ALTO!");
					
					if(numInput < this.top)
						this.top = numInput - 1;
					this.aggiornaTestoAssistenza();
				}
				else
				{
					this.txtAreaResult.setText("NUMERO INDOVINATO!");
					this.disattivaInput();
					this.txtInput.setText("");
					return;
				}
				
				if (this.tentativiRimasti <= 0)
				{
					this.txtAreaResult.setText("TENTATIVI ESAURITI!!\n\nil numero segreto era: " + this.numSegreto);
					this.disattivaInput();
				}
			} 
			else 
			{
				this.txtAreaResult.setText("DEVI INSERIRE UN NUOVO NUMERO VALIDO COMPRESO TRA 1 e " + NMAX + "!");
			}
		} 
		catch (Exception e)
		{
			this.txtAreaResult.setText("DEVI INSERIRE UN NUOVO NUMERO VALIDO COMPRESO TRA 1 e " + NMAX + "!");
		}
		this.txtInput.setText("");
	}
	//
	@FXML
	void initialize()
	{
		assert txtTentativiRimasti != null : "fx:id=\"txtTentativiRimasti\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtTentativiTot != null : "fx:id=\"txtTentativiTot\" was not injected: check your FXML file 'Scene.fxml'.";
		assert pgbBarra != null : "fx:id=\"pgbBarra\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtInput != null : "fx:id=\"txtInput\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnProva != null : "fx:id=\"btnProva\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtAreaResult != null : "fx:id=\"txtAreaResult\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtDifficoltaSelezionata != null : "fx:id=\"txtDifficoltaSelezionata\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtNumeriProvati != null : "fx:id=\"txtNumeriProvati1\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnNuovaPartita != null : "fx:id=\"btnNuovaPartita\" was not injected: check your FXML file 'Scene.fxml'.";
		assert hboxSelectDifficulty != null : "fx:id=\"hboxSelectDifficulty\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtAreaAssistita != null : "fx:id=\"txtAreaAssistita\" was not injected: check your FXML file 'Scene.fxml'.";
	}
}
