/******************************************************************
 * Aplicación para probar la librería jSerialcomm.jar y el entorno de
 * diseño de GUI gráfico. Diseñado para leer cadenas enviadas
 * por la función Serial.println(); de la plataflrma Arduino.
 * Util como plantilla para iniciar un proyecto que comunique
 * Arduino con un PC a traves de puerto COM implementado sobre
 * USB o Bluetooth
 * 
 * Patricio Coronado octubre 2020
 * 
 * Licencia: Este proyecto puede ser utilizado o modificado por
 * cualquiera que lo desee sin compromiso ni responsabilidad de
 * su autor.
 ******************************************************************/
package javacom;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.fazecast.jSerialComm.SerialPort;
import java. util. Arrays;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Toolkit;

public class ComSerial extends JFrame {

	private JPanel contentPane;
	private JTextField puertoAbierto;
	static SerialPort puertoSerie;
	//static int x = 0;
	private static JButton abrirPuerto;
	private static JComboBox<String> listaPuertos;
	private static JTextField recibido;
	private JTextField cadenaEnviar;
	private static JButton botonEnviar;
	
	
	/********************************************************************
	 * Ejecuta la aplicación
	 ********************************************************************/
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					ComSerial frame = new ComSerial();//Llamada al constructor
					frame.setVisible(true);
				} 
				catch (Exception e){e.printStackTrace();}
			}
		});
	}//main
	/*****************************************************************
	 *  Constructor
	 *****************************************************************/
	public ComSerial() 
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage("Conector.png"));
		setTitle("COM Serial");
		//Image icon = new ImageIcon(getClass().getResource("/Conector.png")).getImage();
	    //    setIconImage(icon);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Para cerrar la aplicación
		setBounds(100, 100, 420, 215);
		contentPane = new JPanel();//Instancia el panel
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);//Instancia un layout sobre el panel
		//JComboBox para listar los pueros del sistema
		listaPuertos = new JComboBox<String>();
		listaPuertos.setBounds(163, 28, 92, 22);//Posición del JCombo
		contentPane.add(listaPuertos);
		//Botón para abrir el puerto
		abrirPuerto = new JButton("abrir puerto");
		//Listener del botón abrirPuerto
		abrirPuerto.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {botonAbrirPuertoPulsado();}});
		abrirPuerto.setBounds(10, 28, 131, 23);
		contentPane.add(abrirPuerto);
		//Indicador del puerto abierto
		puertoAbierto = new JTextField();
		puertoAbierto.setBounds(278, 29, 86, 20);
		contentPane.add(puertoAbierto);
		puertoAbierto.setColumns(10);
		puertoAbierto.setEditable(false);
		//Cadena recibida
		recibido = new JTextField();
		recibido.setColumns(10);
		recibido.setBounds(10, 125, 354, 22);
		contentPane.add(recibido);
		recibido.setEditable(false);
		//Cadena que se enviará
		cadenaEnviar = new JTextField();
		cadenaEnviar.setColumns(10);
		cadenaEnviar.setBounds(120, 79, 244, 22);
		contentPane.add(cadenaEnviar);
		//Botón para enviar datos por el puerto
		botonEnviar = new JButton("enviar");
		botonEnviar.addActionListener(new ActionListener() {public void  actionPerformed(ActionEvent e) {botonEnviarPulsado();}});
		botonEnviar.setBounds(10, 79, 100, 23);
		contentPane.add(botonEnviar);
		// Rellena el JComboBox con el nombre de los puertos ordenados previamente
		// Incrementa y decrementa los número de puertos para activar los enteros como tal. Si no hay errores ordenando
		SerialPort[] puertosEnSistema = SerialPort.getCommPorts();//Lista de nombre de puertos
		int[] numeroPuertos;//Para guardar el número de puertos como int para poder ordenarlos
		int nPuertos=puertosEnSistema.length;//Longitud de la lista
		numeroPuertos = new int[nPuertos];//Array de enteros (ordenables) con el número de los puertos
		for(int i = 0; i < nPuertos; i++)//Lee los números de la lista de puertos
			numeroPuertos[i]=Integer.parseInt(puertosEnSistema[i].getSystemPortName().substring(3))+1;
		Arrays.parallelSort(numeroPuertos);//Ordena el array de enteros de números de puertos
		for(int i=0;i<numeroPuertos.length;i++)  	
			listaPuertos.addItem("COM"+(numeroPuertos[i]-1));//Pone los nombres de los puertos en el combo
		botonEnviar.setEnabled(false);//Al principio no se puede enviar
	}
	/*****************************************************************
	 *  Método que responde a la pulsación del botón "Enviar"
	 *****************************************************************/
	protected void botonEnviarPulsado() 
	{
		String comandoEnviar = cadenaEnviar.getText()+'\r';
		byte[] bitesEnviar=comandoEnviar.getBytes();
		int numeroDeBites=bitesEnviar.length;
		if(puertoSerie!=null && puertoSerie.isOpen() && numeroDeBites>1) //Evita enviar a un puerto que no exista o cerrado 
		{
			puertoSerie.writeBytes(bitesEnviar,numeroDeBites);
		}
	}
	/*****************************************************************
	 *  Método que responde a la pulsación del botón "abrir puerto"
	 *****************************************************************/
	protected void botonAbrirPuertoPulsado() 
	{
		if(abrirPuerto.getText().equals("abrir puerto")) //Abre el puerto
		{
			// Selecciona el puerto de la lista
			puertoSerie = SerialPort.getCommPort(listaPuertos.getSelectedItem().toString());
			if(puertoSerie.openPort())//Si consigue abrir el puerto lo configura 
			{
				puertoSerie.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 100);//100ms timeout escritura
				//puertoSerie.setFlowControl(puertoSerie.FLOW_CONTROL_DISABLED);
				puertoSerie.setComPortParameters(115200, 8, 1, 0);
				//Resto de configuraciones 
				abrirPuerto.setText("cerrar puerto");//Cambia el mensaje del botón
				listaPuertos.setEnabled(false);//Inhabilita el JComboBox de puertos
				botonEnviar.setEnabled(true);//Se puede enviar
				//Muestra el puerto abierto
				puertoAbierto.setText(listaPuertos.getSelectedItem().toString());
			}
			/*------------------------------------------------------------
			Crea y ejecuta un thered que escucha el puerto serie abierto
			 y excribe lo recibido en "recibido"
			-------------------------------------------------------------*/
			Thread thread = new Thread()
			{
				@Override public void run() 
				{
					//Clase que lee una linea completa del puerto
					System.out.println(Thread.currentThread().getName());
					Scanner scanner = new Scanner(puertoSerie.getInputStream());
					while(scanner.hasNextLine())//Si encuentra '\n' cerrando la cadena recibida... 
					{
						try {
							String line = scanner.nextLine();
							recibido.setText(line);
							
						} catch(Exception e) {recibido.setText("recibida cadena vacia");}
					}//while
					scanner.close();
				}//run
			};//Thread
			thread.start();//Ejecuta el thread
		}//if abrirPuerto 
		else //Si se desea cerrar el puerto lo hace 
		{
			// disconnect from the serial port
			puertoSerie.closePort();
			listaPuertos.setEnabled(true);
			abrirPuerto.setText("abrir puerto");
			botonEnviar.setEnabled(false);//Se puede enviar
		}
	}// método botonAbrirPuertoPulsado()
}//class ComSerial
