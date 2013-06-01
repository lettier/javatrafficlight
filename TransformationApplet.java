/*

DAVID LETTIER

(C) 2013.

http://www.lettier.com/

*/


import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.lang.*;

public class TransformationApplet extends Applet implements ActionListener
{
	Button startStop;
	BlinkCanvas blinkCanvas;
	
	Button zoomIn, zoomOut, rotateCW, rotateCCW;
	ShapeCanvas shapecanvas;

	public void init( )
	{
		setLayout( new FlowLayout() );
		
		setBackground( Color.LIGHT_GRAY );
		
		blinkCanvas = new BlinkCanvas( );
		add( blinkCanvas );
		
		startStop = new Button( "Blink" );
		add(startStop);
		startStop.addActionListener( this );
		
		shapecanvas = new ShapeCanvas( new PolyShape(), 450, 300 );
		add( shapecanvas );
		
		zoomIn    = new Button( "Zoom In" );
		zoomOut   = new Button( "Zoom Out" );
		rotateCW  = new Button( "Rotate CW" );
		rotateCCW = new Button( "Rotate CCW" );
		add( zoomIn );
		add( zoomOut );
		add( rotateCW );
		add( rotateCCW );
		zoomIn.addActionListener( this );
		zoomOut.addActionListener( this );
		rotateCW.addActionListener( this );
		rotateCCW.addActionListener( this );

		Runnable blinkRunnable = new BlinkRunnable(blinkCanvas);
		Thread t1 = new Thread( blinkRunnable );
		t1.start( );		

		Runnable shapecanvasrunnable = new ShapeCanvasRunnable( shapecanvas );
		Thread t2 = new Thread( shapecanvasrunnable );
		t2.start( );
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == startStop )
		{
			blinkCanvas.setBlinking( !blinkCanvas.isBlinking( ) );
			
			shapecanvas.setBlinking( !shapecanvas.isBlinking( ) );
		
			if (blinkCanvas.isBlinking())
				startStop.setLabel( "Stop" ); 
			else 
				startStop.setLabel( "Blink" );
		}
		
		if ( e.getSource() == zoomIn )
		{
			shapecanvas.zoom( 1.1, 1.1 );
		}
		else if ( e.getSource() == zoomOut )
		{
			shapecanvas.zoom( 0.9, 0.9 );
		}
		else if ( e.getSource() == rotateCW )
		{
			shapecanvas.rotate( 2 );
		}
		else if ( e.getSource() == rotateCCW )
		{
			shapecanvas.rotate( -2 );
		}
	}
}

class BlinkCanvas extends Canvas 
{
	public BlinkCanvas( )
	{
		setSize( 380, 100 );
		setBackground( Color.WHITE ); 
	}

	public void paint( Graphics g ) 
	{
		g.setColor( theColor );
		g.fillOval( 170, 30, 20, 20 );
	}

	public boolean isBlinking( ) { return blinking; }

	public void setBlinking( boolean blinking )
	{
		this.blinking = blinking; 
	}

	public void toggleColor() 
	{
		theColor = ( theColor == Color.ORANGE ) ? getBackground() : Color.ORANGE;
		repaint( );
	}

	Color theColor = Color.ORANGE; 
	boolean blinking = false;
}

class BlinkRunnable implements Runnable 
{
	BlinkRunnable(BlinkCanvas blinkCanvas) 
	{
		this.blinkCanvas = blinkCanvas;
	}

	public void run() 
	{
		while (true)
		{
			if ( blinkCanvas.isBlinking() ) 
			{
				blinkCanvas.toggleColor();
			}
			try 
			{
				Thread.sleep(1000);
			} 
			catch ( InterruptedException e ) {}
		}
	}

	BlinkCanvas blinkCanvas;
}

//// MATRIX /////

class Matrix 
{
	Matrix(int rows, int cols)
	{
		arr = new double[rows][cols];		
	}

	Matrix(double [][] inits)
	{
		arr = new double[ inits.length ][ inits[0].length ];
		for( int mRows = 0 ; mRows < arr.length ; mRows++ )
		{
			for( int mCols = 0 ; mCols < arr[0].length ; mCols++ )
			{
				arr[mRows][mCols] = inits[mRows][mCols];
			}			
		}
	}

	int getNumRows() { return arr.length; }
	int getNumCols() { return arr[0].length; }

	double get(int row, int col)
	{
		return arr[row][col];	 
	}
	
	double[] getRow(int row) 
	{
		return arr[row];
	}

	void set(int row, int col, double val)
	{
		arr[row][col] = val;
	}

	Matrix multiply( Matrix m )	
	{
		if ( arr[0].length != m.getNumRows() )
		{
			System.out.println( "Product AB undefined. Returning passed matrix." );
			return m;
		}
		else
		{
			double[][] temp = new double[ arr.length ][ m.getNumCols() ];
			
			for( int mRows = 0 ; mRows < arr.length ; mRows++ )
			{
				for( int mCols = 0 ; mCols < m.getNumCols() ; mCols++ )
				{
					temp[mRows][mCols] = 0.0;
				}			
			}
			
			for( int mRows = 0 ; mRows < arr.length ; mRows++ )
			{
				for( int mCols = 0 ; mCols < m.getNumCols() ; mCols++ )
				{
					for (int inner = 0; inner < arr[0].length; inner++) 
					{
                			temp[mRows][mCols] += arr[mRows][inner] * m.get( inner , mCols );
           			}
					
				}
			}
			
			return new Matrix( temp );
		}
		
	}

	public String toString()	
	{
		String out = "Matrix " + arr.length + "x" + arr[0].length + ":" + "\n";
		for( int mRows = 0 ; mRows < arr.length ; mRows++ )
		{
			for( int mCols = 0 ; mCols < arr[0].length ; mCols++ )
			{
				out += "[" + arr[mRows][mCols] + "]" + " ";
			}
			out += "\n";
		}
		
		return out;
	}

	double arr[][];
	
	public static void main(String [] args) {
		Matrix 
		m1 = new Matrix(new double [][] {{1, 0}, {0, 1}}),
		m2 = new Matrix(new double [][] {{1, 2}, {3, 4}});
		// Alternatively, you would create the matrix with the first constructor (rows and columns) and then
		// do a series of 'sets':
		//   Matrix m1 = new Matrix(2, 2);
		//      m1.set(0, 0, 1);
		//      m1.set(0, 1, 0);
		//      m1.set(1, 0, 0);
		//      m1.set(1, 1, 1);

		System.out.println(m1);
		System.out.println(m2);
		System.out.println(m2.multiply(m1));

		m1 = new Matrix(new double [][] {{1, 2}, {3, 4}, {5, 6}});
		m2 = new Matrix(new double [][] {{1, 2, 3, 4}, {5, 6, 7, 8}});
		System.out.println(m1);
		System.out.println(m2);
		System.out.println(m1.multiply(m2));
	}
}


////// POLYSHAPE //////

class PolyShape 
{
	public PolyShape()
	{
		double[][] init = {		
							{    0.0,  22.0, 1.0}, // 0
							{   40.0,  22.0, 1.0}, // 1
							{   40.0,   0.0, 1.0}, // 2
							{   56.0,  32.0, 1.0}, // 3
							{   40.0,  64.0, 1.0}, // 4
							{   40.0,  43.0, 1.0}, // 5
							{    0.0,  43.0, 1.0}  // 6
					   };
		
		points = new Matrix( init );
	}

	public static Matrix scale( double xScale, double yScale )
	{
		double[][] scale = {
							{ xScale,      0, 0 },
							{      0, yScale, 0 },
							{      0,      0, 1 }
					    };	
		return new Matrix( scale );    
	}

	public static Matrix rotate( double angle )
	{
		double[][] rotZ = {
							{ Math.cos( angle ), -1*Math.sin( angle ), 0 },
							{ Math.sin( angle ),    Math.cos( angle ), 0 },
							{            0,                         0, 1 }
					    };
					    
		return new Matrix( rotZ );
	}

	public static Matrix translate(double deltaX, double deltaY) 
	{
		double[][] trans = {
							{ 1, 0, deltaX },
							{ 0, 1, deltaY },
							{ 0, 0,      1 }
					    };	
		return new Matrix( trans );
	}
	
	public static Matrix transpose( Matrix m ) // NEEDED TO CONVERT ROW VECTORS TO COLUMN VECTORS FOR MX MULT's
	{
		double[][] t = new double[ m.getNumCols() ][ m.getNumRows() ];
		
		for ( int rows = 0 ; rows < m.getNumRows() ; rows++ )
		{
			for ( int cols = 0 ; cols < m.getNumCols() ; cols++ )
			{
				t[cols][rows] = m.get( rows, cols );
			}
		}
		
		return new Matrix( t );
	}

	public void transform( Matrix transformMatrix )
	{
		Matrix transposed = transpose( points );
		
		Matrix transformed = transformMatrix.multiply( transposed );
		
		points = transpose( transformed );
	}

	public void scaleInPlace(double xScale, double yScale)
	{
		Matrix trans = translate( -1*getAvgX(), -1*getAvgY() );
		
		int origX = getAvgX();
		int origY = getAvgY();
		
		transform( trans );
		
		Matrix scale = scale( xScale, yScale );
		
		transform( scale );
		
		trans = translate( origX, origY );
		
		transform( trans );		
	}

	public void rotateInPlace(double angleInDegrees)
	{
		Matrix trans = translate( -1*getAvgX(), -1*getAvgY() );
		
		int origX = getAvgX();
		int origY = getAvgY();
		
		transform( trans );
		
		Matrix rot = rotate( Math.toRadians( angleInDegrees ) );
		
		transform( rot );
		
		trans = translate( origX, origY );
		
		transform( trans );
		
	}

	public void translateFromPosition( double deltaX, double deltaY )
	{
		Matrix trans = translate( deltaX, deltaY );
		
		transform( trans );
	}

	public int[] getXValues()
	{
		int[] x = new int[ points.getNumRows() ];
		
		for ( int i = 0 ; i < points.getNumRows() ; i++ )
		{
			x[i] = (int) points.get( i, 0 );
		}
		
		return x;
	}

	public int[] getYValues()
	{
		int[] y = new int[ points.getNumRows() ];
		
		for ( int i = 0 ; i < points.getNumRows() ; i++ )
		{
			y[i] = (int) points.get( i, 1 );
		}
		
		return y;
	}

	/** Utils for finding the center point for in-place transformations **/
	public int getAvgX() 
	{
		int avg = 0;
		int i = 0;
		
		for ( i = 0 ; i < points.getNumRows() ; i++ )
		{
			avg += points.get( i, 0 );
		}
		
		return avg/i;		
	}
	
	public int getAvgY() 
	{
		int avg = 0;
		int i = 0;
		
		for ( i = 0 ; i < points.getNumRows() ; i++ )
		{
			avg += points.get( i, 1 );
		}
		
		return avg/i;		
	}

	public String toString()
	{
		String out = "PolyShape centered at (" + getAvgX() + ", " + getAvgY() + "):\n";
		
		for ( int rows = 0 ; rows < points.getNumRows() ; rows++ )
		{

			out += "Point At: (" + points.get(rows,0) + ", " +points.get(rows,1) + ")\n";
		}
		
		return out;
	}
	
	public static void main(String [] args) {
		PolyShape arrow = new PolyShape();
		
		System.out.println( arrow );
		
		arrow.translateFromPosition( 10, 15 );
		
		System.out.println( arrow );
		
		arrow.rotateInPlace( 0 );
		
		System.out.println( arrow );
		
		arrow.scaleInPlace( 2, 1 );
		
		System.out.println( arrow );
		
		arrow.translateFromPosition( -1*arrow.getAvgX(), -1*arrow.getAvgY() );

		System.out.println( arrow );
		
		arrow.scaleInPlace( .5, 1 );
		
		System.out.println( arrow );
	}

	private Matrix points;
}

class ShapeCanvasRunnable implements Runnable 
{
	ShapeCanvasRunnable( ShapeCanvas x ) 
	{
		this.shapecanvas = x;
	}

	public void run() 
	{
		while (true)
		{
			if ( shapecanvas.isBlinking() ) 
			{
				shapecanvas.toggleColor();
			}
			try 
			{
				Thread.sleep(1000);
			} 
			catch ( InterruptedException e ) {}

		}
	}

	ShapeCanvas shapecanvas;
}

//////// ShapeCanvas //////////////////

class ShapeCanvas extends Canvas implements KeyListener, FocusListener, MouseListener {
	public ShapeCanvas( PolyShape x, int w, int h ) 
	{
		shape = x;
		shape.translateFromPosition( 100, 50 );
		addKeyListener(this);
		addFocusListener(this);
		addMouseListener(this);
		setSize( w, h );		
		setBackground( Color.WHITE ); 
	}
	
	public void toggleText( )
	{
		if ( !clicked )
		{
			message = "Click in this area to move the light using arrow keys";
		}
		else
		{
			message = "Use arrow keys to move the light";	
		}
		repaint();
	}

	public void paint(Graphics g)
	{
		g.setColor( Color.BLACK );
		
		g.drawString( message , 10, 20 );		
		
		g.setColor( theColor );
		
		g.drawPolygon( shape.getXValues(), shape.getYValues(), 7);
	}
	
	public boolean isBlinking() { return blinking; }
	public void setBlinking(boolean blinking) { this.blinking = blinking; }
	public void toggleColor() 
	{
		theColor = (theColor == Color.RED) ? getBackground() : Color.RED;
		repaint();
	}
	
	public void zoom( double x, double y )
	{
		shape.scaleInPlace( x, y);
		repaint();
	}
	
	public void rotate( double angle )
	{
		shape.rotateInPlace( angle );
		repaint();
	}	

	public void keyPressed( KeyEvent e )
	{
		int keyCode = e.getKeyCode();
		switch( keyCode ) { 
			case KeyEvent.VK_UP:
				shape.translateFromPosition( 0, -2 );
				repaint();
				break;
			case KeyEvent.VK_DOWN:
				shape.translateFromPosition( 0, 2 );
				repaint();
				break;
			case KeyEvent.VK_LEFT:
				shape.translateFromPosition( -2, 0 );
				repaint();
				break;
			case KeyEvent.VK_RIGHT :
				shape.translateFromPosition( 2, 0 );
				repaint();
				break;
		} 
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void focusGained(FocusEvent evt){ 
		focussed = true; 
		//System.out.println( "Focus gained." );
		clicked = true;
		toggleText();
		repaint();
	}
	public void focusLost(FocusEvent evt){
		focussed = false; 
		//System.out.println( "Focus lost." );
		clicked = false;
		toggleText();
		repaint();
	}
	public void mousePressed(MouseEvent evt) {
		requestFocus();
	}   

	public void mouseEntered(MouseEvent evt) {} 
	public void mouseExited(MouseEvent evt) {}  
	public void mouseReleased(MouseEvent evt) {}
	public void mouseClicked( MouseEvent evt ) 
	{
		clicked = true;
		toggleText( );
	}


	PolyShape shape;

	Color theColor = Color.RED;
	boolean blinking = false;
	boolean focussed = false;
	boolean clicked = false;
	String message = "Click in this area to move the light using arrow keys";
}


