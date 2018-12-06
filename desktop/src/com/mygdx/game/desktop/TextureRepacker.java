package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;

import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.badlogic.gdx.Gdx.input;


/**
 * Created by niz on 15/07/17.
 */

class TextureRepacker {

    private static final String TAG = "texture repacker";
    public static void processGuideSpritesFromSprite(FileHandle file, String name, int w, int h, int colorToMatch){
        processGuideSpritesFromSprite(file, name, w, h, colorToMatch, 0, 0);
    }
    public static void processGuideSpritesFromSprite(FileHandle file, String name, int w, int h, int colorToMatch, int offsetX, int offsetY){
        Pixmap pix = new Pixmap(file);
        int width = pix.getWidth();
        if (width % w != 0) throw new GdxRuntimeException("not cleanly tileable");
        int height = pix.getHeight();
        if (height % h != 0) throw new GdxRuntimeException("not cleanly tileable");
        int ww = width / w;
        int hh = height / h;
        FileHandle outFileint = Gdx.files.internal("guides/" + name);//prefix+layer); // i.e guides/playertorsoguide
        FileHandle outFile = Gdx.files.absolute(outFileint.file().getAbsolutePath());
        DataOutputStream os = new DataOutputStream(outFile.write(false, 100));
        int total = 0;
        for (int k = 0; k < hh; k++)
            for (int i = 0; i < ww; i++)
                for (int x = 0; x < w; x++)
                    for (int y = 0; y < h; y++){
                        int pixel = pix.getPixel(x + i * w, y + k * h);
                        Color.rgba8888ToColor(c, pixel);
                        if (c.a > .5f){
                            if (pixel == colorToMatch)
                            try {
                                GridPoint2 guide = new GridPoint2(x+ offsetX, y + offsetY);
                                os.writeShort(guide.x);
                                os.writeShort(guide.y);
                                total++;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gdx.app.log(TAG, "saved guides " + name + " " + total);

    }

    public static void process(String tiles, String inName) {
       // if (true) return;
        FileHandle atlasFile = Gdx.files.internal(tiles + ".png");
        FileHandle outFile = Gdx.files.absolute(atlasFile.sibling(tiles + "processed.png").file().getAbsolutePath());
        Pixmap inPix = new Pixmap(atlasFile);
        Pixmap inNormals = new Pixmap(Gdx.files.internal(inName + ".png"));
        Pixmap outPix = new Pixmap(inPix.getWidth(), inPix.getHeight(), inPix.getFormat());
        IntArray storedColors = new IntArray(), storedNormals = new IntArray();
        for (int x = 0; x < inPix.getWidth(); x++)
            for (int y = 0; y < inPix.getHeight(); y++){
                int col = inPix.getPixel(x, y);
                c.set(col);
                if (c.a < .1f)
                    col = 0;
                if (!storedColors.contains(col))
                    storedColors.add(col);
                int index = storedColors.indexOf(col);

                int normal = inNormals.getPixel(x, y);
                if (!storedNormals.contains(normal))
                    storedNormals.add(normal);
                int normalIndex = storedNormals.indexOf(normal);

                c.set(0, 0, 0, 1f);
                c.r = toFloat(index);
                c.g = toFloat(normalIndex);
                if (col != 0)outPix.drawPixel(x, y, Color.rgba8888(c));

            }

        PixmapIO.writePNG(outFile, outPix);

        Pixmap indexPix = new Pixmap(128, 2, Pixmap.Format.RGBA8888);
        FileHandle indexOutFile = Gdx.files.absolute(atlasFile.sibling(tiles + "indexTexture.png").file().getAbsolutePath());
        int count;
        for (count = 0; count < storedColors.size; count++){
            c.set(storedColors.get(count));
            indexPix.drawPixel(count, 0, Color.rgba8888(c));
        }

        for (;count < indexPix.getWidth(); count++){
            c.set(Color.MAGENTA);
            indexPix.drawPixel(count, 0, Color.rgba8888(c));
        }
        //Gdx.app.log(TAG, "draw normaraw normaraw normaraw normaraw normaraw normaraw normaraw normaraw normaraw normals" );

        for (int i = 0; i < storedNormals.size; i++){
            c.set(storedNormals.get(i));
            indexPix.drawPixel(i, 1, Color.rgba8888(c));
            //Gdx.app.log(TAG, "draw normal" + c + " / " + storedNormals.size);
        }

        PixmapIO.writePNG(indexOutFile, indexPix);
    }
    static Color c = new Color();
    public static float toFloat(int i){
        return (i + .5f ) / 128f;
    }


    public static void createGuideSpritesIdentical(FileHandle file, String name, int w, int h, GridPoint2 guide) {
        Pixmap pix = new Pixmap(file);
        int width = pix.getWidth();
        if (width % w != 0) throw new GdxRuntimeException("not cleanly tileable");
        int height = pix.getHeight();
        if (height % h != 0) throw new GdxRuntimeException("not cleanly tileable");
        int ww = width / w;
        int hh = height / h;
        FileHandle outFileint = Gdx.files.internal("guides/" + name);//prefix+layer); // i.e guides/playertorsoguide
        FileHandle outFile = Gdx.files.absolute(outFileint.file().getAbsolutePath());
        DataOutputStream os = new DataOutputStream(outFile.write(false, 100));
        for (int i = 0; i < ww * hh; i++){
            try {
                os.writeShort(guide.x);
                os.writeShort(guide.y);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createGuideSpritesIdentical(String name, int total, GridPoint2 guide) {
        FileHandle outFileint = Gdx.files.internal("guides/" + name);//prefix+layer); // i.e guides/playertorsoguide

        FileHandle outFile = Gdx.files.absolute(outFileint.file().getAbsolutePath());
        DataOutputStream os = new DataOutputStream(outFile.write(false, 100));
        for (int i = 0; i < total; i++){
            try {
                os.writeShort(guide.x);
                os.writeShort(guide.y);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}