
import games.perses.game.DrawMode
import games.perses.game.Game
import games.perses.game.Screen
import kotlin.browser.document

/**
 * User: rnentjes
 * Date: 26-3-17
 * Time: 12:46
 */

class JuliaScreen : Screen() {
    val julia = Julia(Game.html)

    override fun loadResources() {
    }

    override fun unloadResources() {
    }

    override fun update(time: Float, delta: Float) {
    }

    override fun render() {
        julia.render()

        //Texts.drawText(300f, Game.view.height / 2f, "Hello Kudens!", font = "bold 62pt Arial", fillStyle = "rgba(255,255,0,0.75)")
    }

}

fun main(args: Array<String>) {
    // set border color
    document.body?.style?.backgroundColor = "#000"

    //Game.view.setToWidth(2000f)
    Game.view.drawMode = DrawMode.NEAREST

    Game.view.minAspectRatio = 0.5f
    Game.view.maxAspectRatio = 2f

    Game.setClearColor(0f, 0f, 0f, 1f)

    Game.start(JuliaScreen())
}
