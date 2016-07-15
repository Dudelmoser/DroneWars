package dronewars.archive;

/**
 *
 * @author Jan David Kleiß
 */
public class BlurredInterval {
    public int a0;
    public int a1;
    public int b1;
    public int b0;
    
    private int da;
    private int db;
    
    public BlurredInterval(int start, int end, int blurStart, int blurEnd) {
        a0 = start;
        a1 = start + blurStart;
        b1 = end - blurEnd;
        b0 = end;
        da = blurStart;
        db = blurEnd;
    }
    
    public BlurredInterval (int start, int end, int blur) {
        if (end - start < blur * 2) {
            blur = (int)Math.floor((end - start) / 2);
        }
        a1 = start + blur;
        b1 = end - blur;
        a0 = start;
        a1 = end;
        da = db = blur;
    }
            
    public BlurredInterval(int start, int end) {
        a0 = a1 = start;
        b1 = b0 = end;
        da = db = 0;
    }
            
    public int valueAt(int p){
	if (p >= a1 && p <= b1) {
            return (int) 255;
        } else if (p < a1) {
            double f = p - a0 / da;
            return (int) (f * 255);
	} else {
            double f = 1 - (p - b1 / db);
            return (int) (f * 255);
	}
    }
}
