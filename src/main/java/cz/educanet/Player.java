package cz.educanet;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Player {
    private final float[] vertices = {
            0.125f, 0.125f, 0f,
            0.125f, -0.125f, 0f,
            -0.125f, -0.125f, 0f,
            -0.125f, 0.125f, 0f,
    };

    private float[] colors = {
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
    };

    private final int[] indices = {
            0, 1, 3,
            1, 2, 3
    };
    private static final float[] textures = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    private final int squareVaoId;
    private final int squareVboId;
    private final int squareEboId;
    private final int colorsId;

    private static int uniformColorLocation;
    private static int uniformMatrixLocation;

    private static int textureIndicesId;
    private static int textureId;

    private static Matrix4f matrix = new Matrix4f()
            .identity();
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public Player() {
        uniformColorLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "outColor");
        uniformMatrixLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");


        squareVaoId = GL33.glGenVertexArrays();
        squareVboId = GL33.glGenBuffers();
        squareEboId = GL33.glGenBuffers();
        colorsId = GL33.glGenBuffers();
        textureIndicesId = GL33.glGenBuffers();
        textureId = GL33.glGenTextures();

        GL33.glBindVertexArray(squareVaoId);

        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length)
                .put(indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareVboId);

        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length)
                .put(vertices)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);


        GL33.glUseProgram(Shaders.shaderProgramId);
        GL33.glUniform3f(uniformColorLocation, 1.0f, 0.0f, 0.0f);

        matrix.get(matrixBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixBuffer);

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0,3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, colorsId);

        FloatBuffer cb = BufferUtils.createFloatBuffer(colors.length)
                .put(colors)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1,3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureIndicesId);

        FloatBuffer tb = BufferUtils.createFloatBuffer(textures.length)
                .put(textures)
                .flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);

        MemoryUtil.memFree(fb);
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getColors() {
        return colors;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getSquareVaoId() {
        return squareVaoId;
    }

    public int getSquareVboId() {
        return squareVboId;
    }

    public int getSquareEboId() {
        return squareEboId;
    }

    public int getColorsId() {
        return colorsId;
    }

    public static Matrix4f getMatrix() {
        return matrix;
    }

    public int getUniformColorLocation() {
        return uniformColorLocation;
    }

    public static int getUniformMatrixLocation() {
        return uniformMatrixLocation;
    }

    public static FloatBuffer getMatrixBuffer() {
        return matrixBuffer;
    }

    public void setColors(float[] newColors) {
        colors = newColors;
    }

    public static int getTextureId() {
        return textureId;
    }

    public static void loadImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer img = STBImage.stbi_load("res/img.png", w, h, comp, 3);
            if (img != null) {
                img.flip();

                GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, w.get(), h.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, img);
                GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

                STBImage.stbi_image_free(img);
            }
        }
    }
    public static Player mainPlayer;
    public static float playerTopLeftX = -0.125f;
    public static float playerTopLeftY = 0.125f;


    public static void init(long window) {
        Shaders.initShaders();
        Player.loadImage();
        createPlayer();
    }

    public static void render(long window) {
        renderPlayer(Player.getMatrix());
    }

    public static void update(long window) {
        movePlayer(window, Player.getMatrix());
    }

    public static void createPlayer() {
        mainPlayer = new Player();
    }

    public static void renderPlayer(Matrix4f matrix) {
        matrix.get(Player.getMatrixBuffer());

        GL33.glUniformMatrix4fv(Player.getUniformMatrixLocation(), false, Player.getMatrixBuffer());
        GL33.glUseProgram(Shaders.shaderProgramId);
        GL33.glBindVertexArray(mainPlayer.getSquareVaoId());
        GL33.glDrawElements(GL33.GL_TRIANGLES, mainPlayer.getVertices().length, GL33.GL_UNSIGNED_INT, 0);
    }


    public static boolean right = true;
    public static boolean up = false;
    public static int timer = 0;
    static float baseSpeed = 0.01f;
    static float slowSpeed = 0.75f * baseSpeed;
    static float fastSpeed = baseSpeed;



    public static void movePlayer(long window, Matrix4f matrix) {
        if (playerTopLeftX > 0.75f) {
            right = false;
        }
        else if (playerTopLeftX < -1f) {
            right = true;
        }

        if (playerTopLeftY > 1f) {
            up = false;
        }
        else if (playerTopLeftY < -0.75f) {
            up = true;
        }

        if (!right && !up) {
            matrix = matrix.translate(-fastSpeed, -slowSpeed, 0f);
            playerTopLeftX -= fastSpeed;
            playerTopLeftY -= slowSpeed;
        }
        else if (!right && up) {
            matrix = matrix.translate(-fastSpeed, slowSpeed, 0f);
            playerTopLeftX -= fastSpeed;
            playerTopLeftY += slowSpeed;
        }
        else if (right && !up) {
            matrix = matrix.translate(fastSpeed, -slowSpeed, 0f);
            playerTopLeftX += fastSpeed;
            playerTopLeftY -= slowSpeed;
        }
        else {
            matrix = matrix.translate(fastSpeed, slowSpeed, 0f);
            playerTopLeftX += fastSpeed;
            playerTopLeftY += slowSpeed;
        }

        timer++;
        if (timer % 100 == 0) {
            System.out.println("X: " + playerTopLeftX + "\t|\tY: " + playerTopLeftY);
            timer = 0;
        }
    }
}
