
import games.perses.game.Game
import games.perses.game.HTMLElements
import games.perses.shader.ShaderProgram
import games.perses.shader.VertextAttributeInfo
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLRenderingContext
import kotlin.js.Date
import kotlin.js.Math
import kotlin.js.Math.random

/**
 * User: rnentjes
 * Date: 21-5-16
 * Time: 17:06
 */

//language=GLSL
private val vertexShader = """
    attribute vec2 a_position;

    uniform vec4 u_viewWindow;

    varying vec2 v_coord;

    void main(void) {
        v_coord = a_position * u_viewWindow.zw + u_viewWindow.xy;

        gl_Position = vec4(a_position, 0.0, 1.0);
    }
"""

//language=GLSL
private val fragmentShader = """
    precision mediump float;

    uniform vec2 u_julia;
    uniform int u_max_iterations;
    uniform float u_color_offset;

    varying vec2 v_coord;

    vec3 hsv2rgb(vec3 c) {
        vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
        vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
        return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
    }

    void main(void) {
        float xx = v_coord.x;
        float yy = v_coord.y;
        float xt = 0.0;
        float sc = xx*xx + yy*yy;

        gl_FragColor = vec4( 0.0, 0.0, 0.0, 1.0);

        if (xx*xx + yy*yy < 4.0) {
            for (int iteration = 0; iteration < 10000; iteration++) {
                if (xx*xx + yy*yy > 4.0 || iteration > u_max_iterations) {
                  float mu = float(iteration) + 1.0 - log(log(xx*xx + yy*yy)) / log(2.0);
                  //mu = sqrt(mu);
                  float ci = sc / float(u_max_iterations);

                  vec3 hsl = vec3(mod(u_color_offset + mu * 13.0, float(u_max_iterations)) / float(u_max_iterations), 1.0, 0.75);
                  vec3 rgb = hsv2rgb(hsl);

                  //float it = mod(mu * 23.0, 768.0);

                  //float red = min(it, 255.0) / 255.0;
                  //float green = max(0.0, min(it, 511.0) - 256.0) / 255.0;
                  //float blue = max(0.0, min(it, 767.0) - 512.0) / 255.0;

                  gl_FragColor = vec4( rgb, 1.0);
                  break;
                }
                xt = xx*xx - yy*yy + u_julia.x;
                yy = 2.0*xx*yy + u_julia.y;
                xx = xt;
                sc += xx*xx + yy*yy;
            }
        }
    }
"""

class JuliaData {
    var juliaX: Float = 0f
    var juliaY: Float = 0f

    var offsetX: Float = 0f
    var offsetY: Float = 0f
    var scaleX: Float = 1f
    var scaleY: Float = 1f

    var max_iterations: Int = 25
    var color_offset: Float = 0f
}

class Julia(val html: HTMLElements) {
    val webgl = html.webgl
    val shaderProgram: ShaderProgram<JuliaData>
    val data: JuliaData = JuliaData()
    val attribBuffer: WebGLBuffer
    val vertices: Float32Array
    val start = Date().getTime()
    val startX = random() - 0.5
    val startY = random() - 0.5
    val maxIterations = (16 + (random() * 48)).toInt()
    var time = 0.0

    init {
        val array: Array<Float> = arrayOf(
          -1f,-1f,
           1f,-1f,
           1f, 1f,
           1f, 1f,
          -1f, 1f,
          -1f,-1f
        )

        vertices = Float32Array(array.size)
        vertices.set(array, 0)

        val setter = { program: ShaderProgram<JuliaData>, data: JuliaData ->
            program.setUniform2f("u_julia", data.juliaX, data.juliaY)
            program.setUniform4f("u_viewWindow", data.offsetX, data.offsetY, data.scaleX, data.scaleY)
            program.setUniform1i("u_max_iterations", data.max_iterations)
            program.setUniform1f("u_color_offset", data.color_offset)
        }

        val vainfo = arrayOf(
          VertextAttributeInfo("a_position", 2)
        )

        shaderProgram = ShaderProgram(webgl, WebGLRenderingContext.TRIANGLES, vertexShader, fragmentShader, vainfo, setter)


        attribBuffer = webgl.createBuffer() ?: throw IllegalStateException("Unable to create webgl buffer!")
        webgl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, attribBuffer);
    }

    fun update(delta: Float) {
        this.time += delta.toDouble()

        data.color_offset -= 6f * delta
    }

    fun render() {
        webgl.clearColor(1f, 1f, 1f, 1f)
        webgl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT)

        data.juliaX = (startX + Math.sin(time / 5.0) / 5f).toFloat()
        data.juliaY = (startY + Math.cos(time / 7.0) / 5f).toFloat()

        //data.juliaX += -0.79f + (Math.sin(time / 101) / 50f).toFloat()
        //data.juliaY += 0.15f + (Math.cos(time / 97) / 50f).toFloat()

        //data.juliaX += 0.28f + (Math.sin(time / 57) / 100f).toFloat()
        //data.juliaY += 0.008f + (Math.cos(time / 53) / 100f).toFloat()

        data.scaleX = 1f //8f - Math.sin(time / 10.0).toFloat() * 0.5f
        data.scaleY = 1f / Game.view.aspectRatio //8f - Math.sin(time / 10.0).toFloat() * 0.5f

        data.max_iterations = maxIterations

        shaderProgram.begin(attribBuffer, data)

        webgl.bufferData(WebGLRenderingContext.ARRAY_BUFFER, vertices, WebGLRenderingContext.DYNAMIC_DRAW);
        webgl.drawArrays(shaderProgram.drawType, 0, 6)

        shaderProgram.end()
    }
}
