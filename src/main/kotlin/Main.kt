
import games.perses.game.DrawMode
import games.perses.game.Game
import games.perses.game.Screen
import games.perses.text.Texts
import kotlin.browser.document
import kotlin.js.Math.max
import kotlin.js.Math.min

/**
 * User: rnentjes
 * Date: 26-3-17
 * Time: 12:46
 */

class JuliaScreen : Screen() {
    val julia = Julia(Game.html)
    var time = 0f

    override fun loadResources() {
    }

    override fun unloadResources() {
    }

    override fun update(time: Float, delta: Float) {
        this.time = time

        julia.update(delta)
    }

    override fun render() {
        julia.render()

        val alpha = max(0f, min(0.75f, 4f - time))
        Texts.drawText(20f, 20f, "Press F5 for another moment of zen.", font = "bold 22pt Arial", fillStyle = "rgba(255,255,255,$alpha)")
    }

}

fun main(args: Array<String>) {
    // set border color
    document.body?.style?.backgroundColor = "#000"

    Game.view.setToWidth(1920f)
    Game.view.drawMode = DrawMode.NEAREST

    Game.view.minAspectRatio = 0.5f
    Game.view.maxAspectRatio = 2f

    Game.setClearColor(0f, 0f, 0f, 1f)

    Game.start(JuliaScreen())
}
