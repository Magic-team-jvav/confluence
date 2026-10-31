// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

/** Scalar port of Ashima Arts' 2D simplex noise (MIT), as used by Spektr.
 * Copyright (C) 2011 Ashima Arts. See THIRD_PARTY_NOTICES.md. */
final class SimplexNoise {
    private SimplexNoise() {}
    private static double mod289(double x) { return x - Math.floor(x / 289) * 289; }
    private static double permute(double x) { return mod289((x * 34 + 1) * x); }
    static double sample(double x, double y) {
        double c0=.211324865405187, c1=.366025403784439, c2=-.577350269189626, c3=.024390243902439;
        double skew=(x+y)*c1, ix=Math.floor(x+skew), iy=Math.floor(y+skew), unskew=(ix+iy)*c0;
        double x0=x-ix+unskew, y0=y-iy+unskew, i1=x0>=y0?1:0, j1=1-i1;
        double sum=corner(ix,iy,x0,y0,c3)
            +corner(ix+i1,iy+j1,x0+c0-i1,y0+c0-j1,c3)
            +corner(ix+1,iy+1,x0+c2,y0+c2,c3);
        return 130*sum;
    }
    private static double corner(double ix,double iy,double x,double y,double c3) {
        double p=permute(permute(mod289(iy))+mod289(ix));
        double gx=2*(p*c3-Math.floor(p*c3))-1, h=Math.abs(gx)-.5, a=gx-Math.floor(gx+.5);
        double m=Math.max(.5-x*x-y*y,0);
        return m*m*m*m*(1.79284291400159-.85373472095314*(a*a+h*h))*(a*x+h*y);
    }
}
