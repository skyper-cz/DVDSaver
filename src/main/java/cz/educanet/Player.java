package cz.educanet;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Player {
    private final int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    public static int uniformMatrixLocation;
    public Matrix4f matrix;
    public FloatBuffer matrixFloatBuffer;
    public int squareVaoId;
    public int squareVboId;
    private final int squareColorId;
    public float[] green;
    public float[] red;
    private final FloatBuffer cfb;
    private float x;
    private float y;
    private final float z;

    public Player(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        matrixFloatBuffer = BufferUtils.createFloatBuffer(16);
        matrix = new Matrix4f().identity();

        green = new float[]{
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
        };
        red = new float[]{
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
        };

        float[] colors = {
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
        };

        float[] vertices = {
                x + z, y, 0.0f,
                x + z, y - z, 0.0f,
                x, y - z, 0.0f,
                x, y, 0.0f,
        };

        cfb = BufferUtils.createFloatBuffer(red.length).put(red).flip();

        squareVaoId = GL33.glGenVertexArrays();
        int squareEboId = GL33.glGenBuffers();
        squareVboId = GL33.glGenBuffers();
        squareColorId = GL33.glGenBuffers();

        uniformMatrixLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");
        GL33.glBindVertexArray(squareVaoId);
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length)
                .put(indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareColorId);
        FloatBuffer cfb = BufferUtils.createFloatBuffer(colors.length).put(colors).flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cfb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1, 4, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);

        MemoryUtil.memFree(cfb);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareVboId);
        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length)
                .put(vertices)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glUseProgram(Shaders.shaderProgramId);
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);

        MemoryUtil.memFree(fb);
        MemoryUtil.memFree(ib);
    }



    public void render() {
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);

        GL33.glUseProgram(Shaders.shaderProgramId);

        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, indices.length, GL33.GL_UNSIGNED_INT, 0);
    }

    public void update(long window) {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            matrix = matrix.translate(0f, 0.01f, 0f);
            this.y = y + 0.01f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            matrix = matrix.translate(0, -0.01f, 0f);
            this.y = y - 0.01f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            matrix = matrix.translate(0.01f, 0f, 0f);
            this.x = x + 0.01f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            matrix = matrix.translate(-0.01f, 0f, 0f);
            this.x = x - 0.01f;
        }
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);
    }

    public float getx() {
        return x;
    }

    public float gety() {
        return y;
    }

    public float getz() {
        return z;
    }

    public void colorgreen() {
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareColorId);
        cfb.put(green).flip();

        GL33.glVertexAttribPointer(1, 4, GL33.GL_FLOAT, false, 0, 0);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cfb, GL33.GL_STATIC_DRAW);
        GL33.glEnableVertexAttribArray(1);
    }

    public void colorred() {
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareColorId);
        cfb.put(red).flip();

        GL33.glVertexAttribPointer(1, 4, GL33.GL_FLOAT, false, 0, 0);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cfb, GL33.GL_STATIC_DRAW);
        GL33.glEnableVertexAttribArray(1);
    }
}
