package com.gdx.extension.util;

import com.badlogic.gdx.graphics.Color;

//by Kyu
public class ColorUtil {

    public static void HSBtoRGB(float hue, float saturation, float brightness, Color color) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        }
        else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        color.set(r/255f, g/255f, b/255f, 1f);
        color.set(
        		0x0000ff | (r << 24) | (g << 16) | (b << 8)
        		);
    }


    public static class HSVColor {

        private int value;

        public float h, s, v;

        public HSVColor(int rgb) {
            value = 0xff000000 | rgb;
        }

        public HSVColor(float hue, float saturation, float brightness) {
			h = hue;
			s = saturation;
			v = brightness;
		}

		public int getValue() {
            return value;
        }

        public int getRed() {
            return (value >> 16) & 0xFF;
        }

        public int getGreen() {
            return (value >> 8) & 0xFF;
        }

        public int getBlue() {
            return (value >> 0) & 0xFF;
        }

        public int getAlpha() {
            return (value >> 24) & 0xff;
        }

        public void setValue(int rgb){
            value = 0xff000000 | rgb;
        }

        public void fromRGB (com.badlogic.gdx.graphics.Color col) {
            float r = col.r;
            float g = col.g;
            float b = col.b;

            float minRGB = Math.min(r,Math.min(g,b));
            float maxRGB = Math.max(r,Math.max(g,b));

            // Black-gray-white
            if (minRGB==maxRGB) {

                h =0;
                s =0;
                v =minRGB;
                return;// [0,0,computedV];
            }

            // Colors other than black-gray-white:
            float d = (r==minRGB) ? g-b : ((b==minRGB) ? r-g : b-r);
            float h = (r==minRGB) ? 3 : ((b==minRGB) ? 1 : 5);
            h = 60*(h - d/(maxRGB - minRGB));
            s = (maxRGB - minRGB)/maxRGB;
            v = maxRGB;

            return;
        }

        public void toRGB(Color col){
        	HSBtoRGB(h,s,v, col);
        }

    }

}