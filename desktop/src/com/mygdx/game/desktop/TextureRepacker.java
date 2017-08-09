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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;

import static com.badlogic.gdx.Gdx.input;


/**
 * Created by niz on 15/07/17.
 */

class TextureRepacker {

    private static final String TAG = "texture repacker";


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
        for (int i = 0; i < storedColors.size; i++){
            c.set(storedColors.get(i));
            indexPix.drawPixel(i, 0, Color.rgba8888(c));
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
}