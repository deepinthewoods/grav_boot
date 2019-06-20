/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.NumberUtils;

/** Draws batched quads using indices.
 * @see Batch
 * @author Niall Quinlan
 * */
public class LineBatchNiz{


    private final ShapeRenderer r;
    private Color color;

    public LineBatchNiz(int i, ShaderProgram shader) {
        r = new ShapeRenderer(i, shader);
    }

    public void setColor(Color color) {
        this.color = color;
        r.setColor(color);
    }

    public Color getColor() {
        return color;
    }

    public void setProjectionMatrix(Matrix4 projectionMatrix) {
        r.setProjectionMatrix(projectionMatrix);
    }



    public void begin() {
        r.begin(ShapeRenderer.ShapeType.Line);
    }

    public void end() {
        r.end();
    }

    public void drawLine(float x, float y, float x2, float y2) {
        r.line(x, y, x2, y2);
    }
    Color c = new Color();


}