/* This project is licensed under the GNU General Public License v3 (GPLv3).
 * A copy of the license can be found at the following link: <https://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package sc.fiji;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Polygon;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import ij.io.FileSaver;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import ij.IJ;
import ij.ImageJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;

import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.RoiListener;

import ij.plugin.PlugIn;
import ij.plugin.frame.PlugInFrame;


/**
 * New.java
 * Purpose: imageJ plugin to measure kymographs
 *
 * @author Han Liu
 * @version v1.0
 */
@SuppressWarnings("serial")
public class New extends PlugInFrame implements PlugIn, ActionListener, ImageListener, RoiListener, KeyListener, MouseListener {
	

	// UI window
	private Frame frame;
	private Label phase;
	private Label distance;
	private Label time;
	private Label rate;
	private Label numCatastrophe;
	private Label numRescue;
	private Label timeGrowth;
	private Label timeShrink;
	private Label frequencyCatastrophe;
	private Label frequencyRescue;
	private Label distanceGrowth;
	private Label distanceShrink;
	private Label xscale;
	private Label yscale;
	private Label pauseAngle;
	private Button drawLeft;
	private Button drawRight;
	private Button drawOverlay;
	private Button output;
	private Button changeX;
	private Button changeY;
	private Button savePath;
	private Button changePause;


	// control variables
	private int side;
	private final int LEFT = 0;
	private final int RIGHT = 1;
	private final int GROWTH = 0;
	private final int SHRINK = 1;
	private final int PAUSE = 2;
	private final int UNDEFINED = -1;
	private double XSCALE = 0.08;
	private double YSCALE = 2.5;
	private double PAUSEANGLE = 3;
	private String savingPath;


	// data indexing
	private final int PHASE = 0;
	private final int DIST = 1;
	private final int TIME = 2;
	private final int RATE = 3;
	private final int NUMCAT = 0;
	private final int NUMRES = 1;
	private final int TGROWTH = 2;
	private final int TSHRINK = 3;
	private final int FREQCAT = 4;
	private final int FREQRES = 5;
	private final int DGROWTH = 6;
	private final int DSHRINK = 7;


	// ROI data
	private Boolean finished;
	private PolygonRoi currentPolyline;
	private Line[] lines;
	private double[][] data;
	private ArrayList<Integer> starts;
	private ArrayList<double[]> data2;
	private int numMicrotubule;


	// image properties
	protected ImagePlus image;
	private String imageDirectory;
	private ImageWindow window;
	private ImageCanvas canvas;
	

	// image overlay
	private Overlay overlayRois;
	private boolean showOverlay;
	static final int OVERLAY_KEY = 17;

	
	/**
	 * Runs the plugin. Initializes UI windows and begins listeners for user input.
	 *
	 * @param arg0 not used.
	 *
	 * @return void.
	 */
	public void run(String arg0) {
		
		// get the associated image parameters
		image = IJ.getImage();
		imageDirectory = IJ.getDir("image");
		savingPath = imageDirectory;
		window = image.getWindow();
		canvas = image.getCanvas();
		showOverlay = true;
		drawRight();
		IJ.run(image, "Line Width...", "line=2");

	}

	/**
	 * Constructs the UI window. Initializes buttons and adds appropriate listeners.
	 * Note this method is ran whenever the plugin is ran.
	 */
	public New() {
		
		super("New");
		if(frame != null) {
			WindowManager.toFront(frame);
			return;
		}
		if (IJ.isMacro()) {
			return;
		}
		
		//set up the UI frame
		frame = this;
		WindowManager.addWindow(this);
		
		frame.setVisible(true);
		frame.setTitle("New Plugin");
		frame.setSize(320, 500);
		
		Panel drawPanel = new Panel();
		Panel statPanel = new Panel();
		Panel settPanel = new Panel();
		Panel funcPanel = new Panel();

		frame.setLayout(new FlowLayout());

		drawPanel.setLayout(new GridLayout());
		statPanel.setLayout(new GridLayout(12,2));
		settPanel.setLayout(new GridLayout(3,1));
		funcPanel.setLayout(new FlowLayout());
		

		// Draw buttons

		drawLeft = new Button("Draw Left");
		drawLeft.addActionListener(this);
		drawPanel.add(drawLeft);

		drawRight = new Button("Draw Right");
		drawRight.addActionListener(this);
		drawPanel.add(drawRight);

		frame.add(drawPanel);


		// Statisitcs

		Panel statTitle = new Panel(new GridLayout());
		Label statLabel = new Label(" Statistics ");
		statLabel.setAlignment(Label.CENTER);
		statTitle.add(new Label("----------"));
		statTitle.add(statLabel);
		statTitle.add(new Label("----------"));
		frame.add(statTitle);

		phase = new Label("0");
		Label phaseLabel = new Label(" Phase: ");
		phaseLabel.setAlignment(Label.RIGHT);
		statPanel.add(phaseLabel);
		statPanel.add(phase);

		distance = new Label("0");
		Label distanceLabel = new Label(" Distance: ");
		distanceLabel.setAlignment(Label.RIGHT);
		statPanel.add(distanceLabel);
		statPanel.add(distance);
		
		time = new Label("0");
		Label timeLabel = new Label(" Time: ");
		timeLabel.setAlignment(Label.RIGHT);
		statPanel.add(timeLabel);
		statPanel.add(time);

		rate = new Label("0");
		Label rateLabel = new Label(" Rate: ");
		rateLabel.setAlignment(Label.RIGHT);
		statPanel.add(rateLabel);
		statPanel.add(rate);
		
		distanceGrowth = new Label("0");
		Label distanceGrowthLabel = new Label(" Distance Growth: ");
		distanceGrowthLabel.setAlignment(Label.RIGHT);
		statPanel.add(distanceGrowthLabel);
		statPanel.add(distanceGrowth);
		
		distanceShrink = new Label("0");
		Label distanceShrinkLabel = new Label(" Distance Shrink: ");
		distanceShrinkLabel.setAlignment(Label.RIGHT);
		statPanel.add(distanceShrinkLabel);
		statPanel.add(distanceShrink);

		timeGrowth = new Label("0");
		Label timeGrowthLabel = new Label(" Time Growth: ");
		timeGrowthLabel.setAlignment(Label.RIGHT);
		statPanel.add(timeGrowthLabel);
		statPanel.add(timeGrowth);
		
		timeShrink = new Label("0");
		Label timeShrinkLabel = new Label(" Time Shrink: ");
		timeShrinkLabel.setAlignment(Label.RIGHT);
		statPanel.add(timeShrinkLabel);
		statPanel.add(timeShrink);
		
		numCatastrophe = new Label("0");
		Label numCatastropheLabel = new Label(" Number of Catastrophe: ");
		numCatastropheLabel.setAlignment(Label.RIGHT);
		statPanel.add(numCatastropheLabel);
		statPanel.add(numCatastrophe);
		
		numRescue = new Label("0");
		Label numRescueLabel = new Label(" Number of Rescue: ");
		numRescueLabel.setAlignment(Label.RIGHT);
		statPanel.add(numRescueLabel);
		statPanel.add(numRescue);

		frequencyCatastrophe = new Label("0");
		Label frequencyCatastropheLabel = new Label(" Frequency Catastrophe: ");
		frequencyCatastropheLabel.setAlignment(Label.RIGHT);
		statPanel.add(frequencyCatastropheLabel);
		statPanel.add(frequencyCatastrophe);
		
		frequencyRescue = new Label("0");
		Label frequencyRescueLabel = new Label(" Frequency Rescue: ");
		frequencyRescueLabel.setAlignment(Label.RIGHT);
		statPanel.add(frequencyRescueLabel);
		statPanel.add(frequencyRescue);

		frame.add(statPanel);


		// Settings and outputs

		Panel settTitle = new Panel(new FlowLayout());
		Label settLabel = new Label(" Settings ");
		settLabel.setAlignment(Label.CENTER);
		settTitle.add(new Label("----------"));
		settTitle.add(settLabel);
		settTitle.add(new Label("----------"));
		frame.add(settTitle);
		
		Panel xscalePanel = new Panel();
		xscalePanel.setLayout(new FlowLayout());
		xscale = new Label(String.valueOf(XSCALE));
		Label xscaleLabel = new Label(" X-scale(μm/pixel): ");
		xscaleLabel.setAlignment(Label.RIGHT);
		xscalePanel.add(xscaleLabel);
		xscalePanel.add(xscale);
		changeX = new Button("Change X-scale");
		changeX.addActionListener(this);
		xscalePanel.add(changeX);
		settPanel.add(xscalePanel);
		
		Panel yscalePanel = new Panel();
		yscalePanel.setLayout(new FlowLayout());
		yscale = new Label(String.valueOf(YSCALE));
		Label yscaleLabel = new Label(" Y-scale(sec/pixel): ");
		yscaleLabel.setAlignment(Label.RIGHT);
		yscalePanel.add(yscaleLabel);
		yscalePanel.add(yscale);
		changeY = new Button("Change Y-scale");
		changeY.addActionListener(this);
		yscalePanel.add(changeY);
		settPanel.add(yscalePanel);

		Panel pausePanel = new Panel();
		pausePanel.setLayout(new FlowLayout());
		pauseAngle = new Label(String.valueOf(PAUSEANGLE));
		Label pauseLabel = new Label(" Pause Angle(°): ");
		pauseLabel.setAlignment(Label.RIGHT);
		pausePanel.add(pauseLabel);
		pausePanel.add(pauseAngle);
		changePause = new Button("Change Pause Angle");
		changePause.addActionListener(this);
		pausePanel.add(changePause);
		settPanel.add(pausePanel);


		// Other functions

		savePath = new Button("Change Save Folder");
		savePath.addActionListener(this);
		funcPanel.add(savePath);

		drawOverlay = new Button("Clear Overlay");
		drawOverlay.addActionListener(this);
		funcPanel.add(drawOverlay);

		output = new Button("Output");
		output.addActionListener(this);
		funcPanel.add(output);

		frame.add(settPanel);
		frame.add(funcPanel);
	}
	
	/***************************************************************************************
	*   Run Debug
	***************************************************************************************/

	/**
	 * Used for testing in Java environment (not in imageJ.) Starts an instance of imageJ, opens a test image, and runs the plugin on the test image.
	 * Note that the directory of the test image must be changed to run on your own computer.
	 *
	 *@param args not used.
	 *
	 * @return void.
	 */
	public static void main(String[] args) {
		
		Class<?> clazz = New.class;
		
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open example stack
		String examplePath = "/Users/mrsata/Desktop/microtubules/Kymograph examples_Han/500 nM SPR1-GFP Slide1 m2_1.tif";
		ImagePlus image = IJ.openImage(examplePath);
		image.show();

		// run the test plugin
		IJ.runPlugIn(clazz.getName(), "");
	}

	/***************************************************************************************
	*   Button events
	***************************************************************************************/
	
	/**
	 * Restart drawing.
	 *
	 * @return void.
	 */
	private void redraw() {

		// clear overlay
		if (overlayRois != null) {
			overlayRois.clear();
		}
		image.updateAndDraw();
		image.changes = false;

		// empty data
		currentPolyline = null;
		lines = null;
		data = null;
		starts = null;
		data2 = null;
		numMicrotubule = 0;
		finished = true;

		// clear display
		clearDisplay();

		// reset listeners
		removeListeners();
		addListeners();

	}

	private void clearDisplay() {
		phase.setText("0");
		distance.setText("0");
		time.setText("0");
		rate.setText("0");
		numCatastrophe.setText("0");
		numRescue.setText("0");
		timeGrowth.setText("0");
		timeShrink.setText("0");
		frequencyCatastrophe.setText("0");
		frequencyRescue.setText("0");
		distanceGrowth.setText("0");
		distanceShrink.setText("0");
		xscale.setText(String.valueOf(XSCALE));
		yscale.setText(String.valueOf(YSCALE));
	}

	/**
	 * Start new drawing on the left side.
	 *
	 * @return void.
	 */
	private void drawLeft() {
		if (image == null) return;
		image.deleteRoi();
		IJ.setTool("polyline");
		side = LEFT;
		drawLeft.setForeground(Color.LIGHT_GRAY);
		drawRight.setForeground(Color.BLACK);
		redraw();
		IJ.log("Start new drawing on the left side");
	}

	/**
	 * Start new drawing on the right side.
	 *
	 * @return void.
	 */
	private void drawRight() {
		if (image == null) return;
		image.deleteRoi();
		IJ.setTool("polyline");
		side = RIGHT;
		drawRight.setForeground(Color.LIGHT_GRAY);
		drawLeft.setForeground(Color.BLACK);
		redraw();
		IJ.log("Start new drawing on the right side");
	}

	/**
	 * Change the scale for X-axis.
	 *
	 * @return void.
	 */
	private void changeXScale() {
		double newXscale = IJ.getNumber("X-scale", XSCALE);
		if (newXscale <= 0) {
			IJ.error("X-scale must be positive");
		}
		else{
			XSCALE = newXscale;
			xscale.setText(String.valueOf(XSCALE));
			IJ.log("X-scale changed");
		}
	}

	/**
	 * Change the scale for Y-axis.
	 *
	 * @return void.
	 */
	private void changeYScale() {
		double newYscale = IJ.getNumber("Y-scale", YSCALE);
		if (newYscale <= 0) {
			IJ.error("Y-scale must be positive");
		}
		else{
			YSCALE = newYscale;
			yscale.setText(String.valueOf(YSCALE));
			IJ.log("Y-scale changed");
		}
	}

	/**
	 * Change the pause angle.
	 *
	 * @return void.
	 */
	private void changePauseAngle() {
		double newPauseAngle = IJ.getNumber("Pause Angle", PAUSEANGLE);
		if (newPauseAngle <= 0 || newPauseAngle >= 180) {
			IJ.error("Pause Angle must be within 0 to 180 degree");
		}
		else{
			PAUSEANGLE = newPauseAngle;
			pauseAngle.setText(String.valueOf(PAUSEANGLE));
			IJ.log("Pause Angle changed");
		}
	}

	/**
	 * Start new drawing on the right side.
	 *
	 * @return void.
	 */
	private void changeSavingPath() {
		String newSavingPath = IJ.getDirectory("Saving Location");
		if (newSavingPath != null) {
			savingPath = newSavingPath;
			IJ.log("Saving location changed to: " + savingPath);
		}
	}

	/**
	 * Change overlay show option.
	 *
	 * @return void.
	 */
	private void changeOverlayShowOption(){
		showOverlay = !showOverlay;
		drawOverlay.setLabel(showOverlay ? "Clear Overlay" : "Show Overlay");
		IJ.log(showOverlay ? "Show Overlay" : "Clear Overlay");
		draw();
	}
	
	/**
	 * Adds listeners for key framing and anchor point selection.
	 *
	 * @return void.
	 */
	private void addListeners() {
		
		if(window != null) {
			window.addKeyListener(this);
		}
		
		if(canvas != null) {
			canvas.addKeyListener(this);
		}
		
		
		ImagePlus.addImageListener(this);

		Roi.addRoiListener(this);
		
		// IJ.log("added listeners");
	}
	
	/**
	 * Removes listeners for key framing and anchor point selection.
	 *
	 * @return void.
	 */
	private void removeListeners() {
        
		if (window!=null) {
            window.removeKeyListener(this);
        }
		
        if (canvas!=null) {
            canvas.removeKeyListener(this);
        }
		
        ImagePlus.removeImageListener(this);
        
        Roi.removeRoiListener(this);
        
        // IJ.log("removed listeners");
	}

	/**
	 * For UI button presses. Calls the respective method for each button.
	 *
	 * @return void.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String label = e.getActionCommand();
		
		if (label == "Draw Left"){
			drawLeft();
		}
		else if (label == "Draw Right"){
			drawRight();
		}
		else if (label == "Change X-scale"){
			changeXScale();
		}
		else if (label == "Change Y-scale"){
			changeYScale();
		}
		else if (label == "Change Pause Angle"){
			changePauseAngle();
		}
		else if (label == "Change Saving Location"){
			changeSavingPath();
		}
		else if (label == "Clear Overlay" || label == "Show Overlay"){
			changeOverlayShowOption();
		}
		else if (label == "Output"){
			output();
		}
		else {
			IJ.error("Invalid ActionEvent in actionPerformed");
		}
	}

	/***************************************************************************************
	*   Functionality
	***************************************************************************************/

	public void getLines() {

		int n = currentPolyline.getNCoordinates();
		Polygon currentPolygon = currentPolyline.getPolygon();
		int[] x = currentPolygon.xpoints;
		int[] y = currentPolygon.ypoints;
		if (finished) n+=1;
		if (n > 2) {
			lines = new Line[n-2];
			for (int i=0; i<(n-2); i++) {
				Line line = new Line(x[i], y[i], x[i+1], y[i+1]);
				lines[i] = line;
			}
		}

	}

	public int getPhase(Line line){
		int[] x = {line.x1, line.x2};
		int[] y = {line.y1, line.y2};
		if (y[0] >= y[1]) return UNDEFINED;
		if (side == LEFT) {
			if (Math.abs(x[1] - x[0]) < PAUSEANGLE) {
				return PAUSE;
			} else if (x[0] < x[1]){
				return SHRINK;
			} else if (x[0] > x[1]){
				return GROWTH;
			} else {
				return UNDEFINED;
			}
		}
		else if (side == RIGHT) {
			if (Math.abs(x[1] - x[0]) < PAUSEANGLE) {
				return PAUSE;
			} else if (x[0] > x[1]){
				return SHRINK;
			} else if (x[0] < x[1]){
				return GROWTH;
			} else {
				return UNDEFINED;
			}
		}
		else {
			IJ.error("Phase error: side not set");
			int i = -2;
			return i;
		}
		
	}

	public String phase2String(double phase){
		String phaseString = "";
		switch ((int)phase)
		{
			case GROWTH: phaseString = "growth";
				break;
			case SHRINK: phaseString = "shrink";
				break;
			case PAUSE: phaseString = "pause";
				break;
			case UNDEFINED: phaseString = "undefined";
				break;
		}
		return phaseString;
	}

	public void calculate() {

		if (lines != null){
			boolean horizontal = false;
			for (int i=0; i<lines.length; i++) {
				if (getPhase(lines[i]) == UNDEFINED) horizontal = true;
			}
			if (horizontal) IJ.log("Warning: undefined horizontal/upward line drawn");
		}
		int n = currentPolyline.getNCoordinates();
		if (finished) n+=1;
		if (n < 3) return;
		n = lines.length;

		data = new double[14][n];

		for (int i=0; i<n; i++) {
			Line line = lines[i];
			int width = Math.abs(line.x2 - line.x1);
			int height = line.y2 - line.y1;	
			data[PHASE][i] = getPhase(line);
			data[DIST][i] = width * XSCALE;
			data[TIME][i] = height * YSCALE;
			data[RATE][i] = data[DIST][i] / data[TIME][i];
		}

		starts = new ArrayList<Integer>();
		data2 = new ArrayList<double[]>();
	
		int start = 0;
		starts.add(start);

		for (int i=0; i<n; i++) {
			double[] d = new double[8];
			if (data[PHASE][i] == PAUSE || i == n-1){
				for (int j=start; j < i+1; j++) {
					if (data[PHASE][j] == GROWTH){
						d[TGROWTH] += data[TIME][j];
						d[DGROWTH] += data[DIST][j];
						if (j > 1 && data[PHASE][j-1] == SHRINK) d[NUMRES] += 1;
					} else if (data[PHASE][j] == SHRINK) {
						d[TSHRINK] += data[TIME][j];
						d[DSHRINK] += data[DIST][j];
						d[NUMCAT] += 1;
					}
				}
				if (d[TGROWTH] != 0) d[FREQCAT] = d[NUMCAT]/d[TGROWTH];
				if (d[TSHRINK] != 0) d[FREQRES] = d[NUMRES]/d[TSHRINK];
				if (i < n-1) {
					start = i+1;
					starts.add(start);
				}
				data2.add(d);
			}
		}
		numMicrotubule = data2.size();
		return;
	}

	public void display(){
		if (lines == null) return;
		int n = lines.length;
		phase.setText(phase2String(data[PHASE][n-1]));
		distance.setText(String.valueOf(data[DIST][n-1]));
		time.setText(String.valueOf(data[TIME][n-1]));
		rate.setText(String.valueOf(data[RATE][n-1]));
		if (data2 == null) return;
		double[] currData2 = new double[8];
		if (data[PHASE][n-1] != PAUSE) currData2 = data2.get(numMicrotubule-1);
		numCatastrophe.setText(String.valueOf(currData2[NUMCAT]));
		numRescue.setText(String.valueOf(currData2[NUMRES]));
		timeGrowth.setText(String.valueOf(currData2[TGROWTH]));
		timeShrink.setText(String.valueOf(currData2[TSHRINK]));
		frequencyCatastrophe.setText(String.valueOf(currData2[FREQCAT]));
		frequencyRescue.setText(String.valueOf(currData2[FREQRES]));
		distanceGrowth.setText(String.valueOf(currData2[DGROWTH]));
		distanceShrink.setText(String.valueOf(currData2[DSHRINK]));
	}

	private void draw() {
		
		if (lines != null) {

			if (showOverlay) {

				overlayRois = new Overlay();

				for (int i=0; i<lines.length; i++) {
					Line l = lines[i];
					switch (getPhase(l)) {
						case GROWTH: l.setStrokeColor(Color.green); break;
						case SHRINK: l.setStrokeColor(Color.red); break;
						case PAUSE: l.setStrokeColor(Color.blue); break;
						case UNDEFINED: l.setStrokeColor(new Color(0, 0, 0, 0.0f)); break;
					}
					overlayRois.add(l);
				}
				image.setOverlay(overlayRois);
				image.updateAndDraw();
				image.changes = true;

			} else {

				if (overlayRois != null) {
					overlayRois.clear();
				}
				image.updateAndDraw();
				image.changes = false;

			}
		}

	}

	private void output() {
		
		if (lines != null && image.isVisible()) {
			String timeStamp = new SimpleDateFormat(".MM.dd.HH.mm").format(new Date());
			String label = image.getTitle();
			String imagePath = savingPath + label + timeStamp + ".tif";
			String filePath = savingPath + label + timeStamp + ".csv";
			if (image.changes) {
				FileSaver saver = new FileSaver(image);
				boolean success = saver.saveAsTiff(imagePath);
				if (success) IJ.log("Output: Image saved.");
			}
			try (PrintWriter out = new PrintWriter(filePath)) {

				String label1 = "Index, Label, Phase, Distance(um), Time(s), "
					+ "Rate growth(um/s), Rate shrink(um/s), ";
				String label2 = "Distance growth(um), Distance shrink(um), Time growth(s), Time shrink(s), "
					+ "Catastrophe, Rescue, Catastrophe frequency, Rescue frequency";
				out.println(label1 + label2);

				for (int i=0; i<lines.length; i++) {
					// Line l = lines[i];
					String o = String.valueOf(i+1) + "," + label + ",";

					o += phase2String(data[PHASE][i]) + ",";
					o += String.valueOf(data[DIST][i]) + ",";
					o += String.valueOf(data[TIME][i]) + ",";
					if (data[PHASE][i] == GROWTH) o += String.valueOf(data[RATE][i]) + ",,";
					else if (data[PHASE][i] == SHRINK) o += "," + String.valueOf(data[RATE][i]) + ",";
					else o += ",,";

					if (data2 != null) {
						if(starts.contains(i)){
							double[] currData2 = data2.get(starts.indexOf(i));
							o += String.valueOf(currData2[DGROWTH]) + ",";
							o += String.valueOf(currData2[DSHRINK]) + ",";
							o += String.valueOf(currData2[TGROWTH]) + ",";
							o += String.valueOf(currData2[TSHRINK]) + ",";
							o += String.valueOf(currData2[NUMCAT]) + ",";
							o += String.valueOf(currData2[NUMRES]) + ",";
							o += String.valueOf(currData2[FREQCAT]) + ",";
							o += String.valueOf(currData2[FREQRES]) + ",";
						}
					}

					out.println(o);
				}
				
				IJ.log("Output: Analysis saved.");
			} catch (FileNotFoundException e){
				IJ.log("ERROR: File not found.");
			}
		}

	}


	/***************************************************************************************
	*   Listeners
	***************************************************************************************/

	/**
	 * Notified by RoiListener when an event occurs. Used to record and update key frames when a ROI is modified.
	 * 
	 * @param imp the image associated with the ROI that was modified
	 * @param id the type of ROI event
	 */
	@Override
	public void roiModified(ImagePlus imp, int id) {
		
		if(imp == image) {
			
			String type = "UNKNOWN";
			
	        switch (id) {
		        case CREATED: type="CREATED";
		        	break;
		        case MOVED: type="MOVED";
		        	break;
		        case MODIFIED: type="MODIFIED";
		        	break;
		        case EXTENDED: type="EXTENDED";
		        	break;
		        case COMPLETED: type="COMPLETED";
		        	break;
		        case DELETED: type="DELETED";
		        	break;
			}

			if (id != MODIFIED) IJ.log("ROI event: " + type);

			if (id == CREATED) {
				finished = false;
			}
	        else if (id == EXTENDED || id == MODIFIED || id == COMPLETED || id == MOVED) {
	        	Roi currentRoi = imp.getRoi();	        	
	        	if(currentRoi != null) {
					if (currentRoi.getType() == Roi.POLYLINE) {
						if (id == COMPLETED) finished = true;
						currentPolyline = (PolygonRoi) currentRoi;
						getLines();
						calculate();
						display();
						draw();
					}
					else {
						IJ.error("RoiListener error: please use polyline tool");
					}
				}
			}
			else if (id == DELETED) {
				redraw();
				IJ.log("Start new drawing");
			}
	        else {
	        	IJ.log("Did not record ROI event: " + type);
	        }	
		}
	}
	
	/**
	 * Notified by ImageListener when an image is updated.
	 * 
	 * @param ip the image that was updated
	 */
	@Override
	public void imageUpdated(ImagePlus ip) {}
	
	@Override
	public void imageClosed(ImagePlus ip) {
		IJ.log("Image closed");
		clearDisplay();
	}

	@Override
	public void imageOpened(ImagePlus arg0) {
		IJ.log("Image opened");
		// get the associated image parameters
		image = IJ.getImage();
		imageDirectory = IJ.getDir("image");
		window = image.getWindow();
		canvas = image.getCanvas();
		redraw();
	}

	/**
	 * Notified by KeyListener when an event occurs. Used to change overlay show option. 
	 * 
	 * @param e the KeyEvent generated by a key press
	 */
	@Override
	public void keyPressed(KeyEvent e) {

		int keyCode = e.getKeyCode();
		
		if(keyCode == OVERLAY_KEY) { // if "Control" is pressed
        	
			changeOverlayShowOption();
			
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	

	/**
	 * Notified by WindowListener when the plugin UI is closed. Cleans up the plugin so that it can be ran again. 
	 * 
	 * @param e the WindowEvent corresponding to closing the UI window
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		IJ.log("Plugin closed");
		removeListeners();
		frame = null;
	}

	/**
	 * Notified by MouseListener when the mouse is clicked. 
	 * 
	 * @param e the MouseEvent corresponding to the mouse click
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
