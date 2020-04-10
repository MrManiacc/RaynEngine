package com.jgfx.testing;

import com.jgfx.assets.naming.Name;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.All;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.gui.components.text.FontCmp;
import com.jgfx.gui.components.text.TextCmp;
import com.jgfx.gui.elements.containers.IContainer;
import com.jgfx.gui.elements.containers.Root;
import com.jgfx.gui.elements.containers.stack.HStack;
import com.jgfx.gui.elements.controls.Image;
import com.jgfx.gui.elements.controls.Text;
import com.jgfx.gui.elements.containers.stack.VStack;
import com.jgfx.gui.enums.Colors;
import com.jgfx.gui.enums.Fonts;
import lombok.SneakyThrows;

import java.io.File;

/**
 * Here we test the menu to make sure things are working
 */
@AutoRegister
public class MenuTesting extends EntitySystem {
    @All({TextCmp.class, FontCmp.class}) Group textElements;
    //The root container
    @In Root root;

    @Override
    public void initialize() {
        createElements();
    }

    /**
     * We create some elements here
     */
    @SneakyThrows
    private void createElements() {
        root.with(
                () -> new VStack()
                        .center()
                        .background(Colors.SILVER)
                        .with(
                                //Order defines depth
                                () -> new Image(new Name("white"))//An image in the top left, and 20 pixels away from the edges
                                        .topLeading()
                                        .marginLeft(20)
                                        .marginTop(20),
                                () -> new Text("Header font")//A font that is a header in the center of the vstack
                                        .font(Fonts.H1)
                                        .bold()
                                        .center(),
                                () -> new Text("default font")
                        ),
                () -> new HStack()
                        .center()
                        .with(
                                () -> new Image("white"),
                                () -> new Image("white")
                                        .overlay(Colors.SUN_FLOWER, 200)
                        )
        );
        root.build();
        //Floating centered text
        root.with(() -> new Text("Floating text").center()).build();
    }
}
