package com.example.leehyungyu.draw_example;

/**
 * Created by LeeHyunGyu on 2018-06-07.
 */

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;

import su.levenetc.android.textsurface.Text;
import su.levenetc.android.textsurface.TextBuilder;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.animations.Alpha;
import su.levenetc.android.textsurface.animations.ChangeColor;
import su.levenetc.android.textsurface.animations.Circle;
import su.levenetc.android.textsurface.animations.Delay;
import su.levenetc.android.textsurface.animations.Parallel;
import su.levenetc.android.textsurface.animations.Rotate3D;
import su.levenetc.android.textsurface.animations.Sequential;
import su.levenetc.android.textsurface.animations.ShapeReveal;
import su.levenetc.android.textsurface.animations.SideCut;
import su.levenetc.android.textsurface.animations.Slide;
import su.levenetc.android.textsurface.animations.TransSurface;
import su.levenetc.android.textsurface.contants.Align;
import su.levenetc.android.textsurface.contants.Direction;
import su.levenetc.android.textsurface.contants.Pivot;
import su.levenetc.android.textsurface.contants.Side;

public class CreditText {

    public static void play(TextSurface textSurface) {

//        final Typeface robotoBlack = Typeface.createFromAsset(assetManager, "fonts/Roboto-Black.ttf");
        Paint paint = new Paint();
        paint.setAntiAlias(true);
//        paint.setTypeface(robotoBlack);

        Text textDaai = TextBuilder
                .create("JAVA Project")
                .setPaint(paint)
                .setSize(64)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.SURFACE_CENTER).build();

        Text textBraAnies = TextBuilder
                .create("OH! Shit!   ")
                .setPaint(paint)
                .setSize(44)
                .setAlpha(0)
                .setColor(Color.parseColor("#fae407"))
                .setPosition(Align.BOTTOM_OF, textDaai).build();

        Text textFokkenGamBra = TextBuilder
                .create("made by")
                .setPaint(paint)
                .setSize(44)
                .setAlpha(0)
                .setColor(Color.parseColor("#fae407"))
                .setPosition(Align.RIGHT_OF, textBraAnies).build();

        Text textHaai = TextBuilder
                .create("진창엽")
                .setPaint(paint)
                .setSize(50)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.BOTTOM_OF, textFokkenGamBra).build();

        Text textDaaiAnies = TextBuilder
                .create("이현규")
                .setPaint(paint)
                .setSize(50)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textHaai).build();

        Text texJungho = TextBuilder
                .create("김정호")
                .setPaint(paint)
                .setSize(50)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textDaaiAnies).build();

        Text textYoonHo = TextBuilder
                .create("장윤호")
                .setPaint(paint)
                .setSize(50)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, texJungho).build();

        Text textSangHyun = TextBuilder
                .create("배상현")
                .setPaint(paint)
                .setSize(50)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textYoonHo).build();

        Text texThyLamInnie = TextBuilder
                .create("   using")
                .setPaint(paint)
                .setSize(44)
                .setAlpha(0)
                .setColor(Color.WHITE)
                .setPosition(Align.RIGHT_OF, textSangHyun).build();

        Text textThrowDamn = TextBuilder
                .create("   Android")
                .setPaint(paint)
                .setSize(44)
                .setAlpha(0)
                .setColor(Color.parseColor("#fae407"))
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, texThyLamInnie).build();

        Text textDevilishGang = TextBuilder
                .create("   Apache")
                .setPaint(paint)
                .setSize(44)
                .setAlpha(0)
                .setColor(Color.parseColor("#fae407"))
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textThrowDamn).build();

        Text textSignsInTheAir = TextBuilder
                .create("   MYSQL")
                .setPaint(paint)
                .setSize(44)
                .setAlpha(0)
                .setColor(Color.parseColor("#fae407"))
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textDevilishGang).build();

        textSurface.play(
                new Sequential(
                        ShapeReveal.create(textDaai, 750, SideCut.show(Side.LEFT), false),
                        new Parallel(ShapeReveal.create(textDaai, 600, SideCut.hide(Side.LEFT), false), new Sequential(Delay.duration(300), ShapeReveal.create(textDaai, 600, SideCut.show(Side.LEFT), false))),
                        new Parallel(new TransSurface(500, textBraAnies, Pivot.CENTER), ShapeReveal.create(textBraAnies, 1300, SideCut.show(Side.LEFT), false)),
                        Delay.duration(500),
                        new Parallel(new TransSurface(750, textFokkenGamBra, Pivot.CENTER), Slide.showFrom(Side.LEFT, textFokkenGamBra, 750), ChangeColor.to(textFokkenGamBra, 750, Color.WHITE)),
                        Delay.duration(500),
                        new Parallel(TransSurface.toCenter(textHaai, 500), Rotate3D.showFromSide(textHaai, 750, Pivot.TOP)),
                        new Parallel(TransSurface.toCenter(textDaaiAnies, 500), Slide.showFrom(Side.TOP, textDaaiAnies, 500)),
                        new Parallel(TransSurface.toCenter(texJungho, 500), Slide.showFrom(Side.TOP, texJungho, 500)),
                        new Parallel(TransSurface.toCenter(textYoonHo, 500), Slide.showFrom(Side.TOP, textYoonHo, 500)),
                        new Parallel(TransSurface.toCenter(textSangHyun, 500), Slide.showFrom(Side.TOP, textSangHyun, 500)),
                        new Parallel(TransSurface.toCenter(texThyLamInnie, 750), Slide.showFrom(Side.LEFT, texThyLamInnie, 500)),
                        Delay.duration(500),
                        new Parallel(
                                new TransSurface(1500, textSignsInTheAir, Pivot.CENTER),
                                new Sequential(
                                        new Sequential(ShapeReveal.create(textThrowDamn, 500, Circle.show(Side.CENTER, Direction.OUT), false)),
                                        new Sequential(ShapeReveal.create(textDevilishGang, 500, Circle.show(Side.CENTER, Direction.OUT), false)),
                                        new Sequential(ShapeReveal.create(textSignsInTheAir, 500, Circle.show(Side.CENTER, Direction.OUT), false))
                                )
                        ),
                        Delay.duration(200),
                        new Parallel(
                                ShapeReveal.create(textThrowDamn, 1500, SideCut.hide(Side.LEFT), true),
                                new Sequential(Delay.duration(250), ShapeReveal.create(textDevilishGang, 1500, SideCut.hide(Side.LEFT), true)),
                                new Sequential(Delay.duration(500), ShapeReveal.create(textSignsInTheAir, 1500, SideCut.hide(Side.LEFT), true)),
                                Alpha.hide(textSangHyun,1500),
                                Alpha.hide(texThyLamInnie, 1500),
                                Alpha.hide(textDaaiAnies, 1500)
                        )
                )
        );

    }
}
