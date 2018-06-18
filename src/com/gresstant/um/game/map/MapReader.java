package com.gresstant.um.game.map;

import com.gresstant.um.game.object.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.function.*;

public class MapReader {
    MapReaderContext mrContext;

    public MapReader(MapReaderContext context) {
        mrContext = context;
    }

    public MapBase read(File f) throws BadFormatException, IOException {
        FileInputStream fis = new FileInputStream(f);

        if (fis.read() == 0x32 &&
                fis.read() == 0x8D &&
                fis.read() == 0xB4 &&
                fis.read() == 0x50 &&
                fis.read() == 0x4D &&
                fis.read() == 0x47) {
            return _328DB4(fis);
        }

        throw new BadFormatException();
    }

    public static short readShort(InputStream is) throws BadFormatException, IOException {
        int a = is.read();
        int b = is.read();
        if (a == -1 || b == -1) throw new BadFormatException();
        return (short) (a << 8 | b);
    }

    public static int readInt(InputStream is) throws BadFormatException, IOException {
        int a = readShort(is);
        int b = readShort(is);
        return a << 16 | b;
    }

    public static long readLong(InputStream is) throws BadFormatException, IOException {
        long a = readInt(is);
        long b = readInt(is);
        return a << 32 | b;
    }

    private Function<Point2D.Double, IEntity> _328DB4_FactoryFactory(IEntity generated) {
        return (point) -> {
            generated.setTop(point.y - generated.getHeight());
            generated.setLeft(point.x);
            generated.activate();
            return generated;
        };
    }

    private MapBase _328DB4(FileInputStream fis) throws BadFormatException, IOException {
        for (int i = 0; i < 2; i++) // 0x0006, 0x0007
            fis.read(); // 保留两位供以后使用

        Map_0_0 output = new Map_0_0();

        output.width = readShort(fis); // 0x0008, 0x0009
        if (output.width < 400)
            throw new BadFormatException();

        output.marioX = readShort(fis); // 0x000A, 0x000B
        output.marioY = readShort(fis); // 0x000C, 0x000D

        for (int i = 0; i < 3; i++) fis.read();// 0x000E, 0x000F, 0x0010

        int colorR = fis.read(); // 0x0011
        int colorG = fis.read(); // 0x0012
        int colorB = fis.read(); // 0x0013
        output.bgColor = new Color(colorR, colorG, colorB);

        output.winX = readShort(fis); // 0x0014, 0x0015

        for (int i = 0; i < 2; i++) fis.read();// 0x0016, 0x0017

        boolean normal = false; // 指示是否正常退出
        List<Point2D.Double> chkpointBuffer = new ArrayList<>();
        int got;
        outer: while ((got = fis.read()) != -1) {
            switch (got) {
                case 0x00: {
                    normal = true;
                    break outer;
                }
                case 0x01: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.blocks.add(new FragileBlock(mrContext.context, x, y));
                    break;
                }
                case 0x02: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    int appearanceID = fis.read();
                    int generateID = fis.read();

                    BufferedImage[] appearance;
                    //region assigning appearance
                    switch (appearanceID) {
                        case -1:
                            throw new BadFormatException();
                        case 0x00:
                            appearance = mrContext.context.imgRes.getResource("QUESTION$NORMAL$STAND");
                            break;
                        case 0x01:
                            appearance = mrContext.context.imgRes.getResource("PALEBLOCK$NORMAL$STAND");
                            break;
                        case 0x02:
                            appearance = new BufferedImage[] {null};
                            break;
                        default:
                            throw new BadFormatException();
                    }
                    //endregion
                    Function<Point2D.Double, IEntity> factory;
                    //region assigning factory
                    switch (generateID) {
                        case -1:
                            throw new BadFormatException();
                        case 0x00:
                            throw new RuntimeException("coin not implemented");
                        case 0x01:
                            factory = _328DB4_FactoryFactory(mrContext.player.getGrowth() == Mario.GrowthState.SMALL ?
                                    new RedMushroom(mrContext.context, true, 0, 0) :
                                    new Flower(mrContext.context, 0, 0));
                            break;
                        case 0x02:
                            factory = _328DB4_FactoryFactory(new GreenMushroom(mrContext.context, true, 0, 0, mrContext.lifeIncrement));
                            break;
                        case 0x03:
                            factory = _328DB4_FactoryFactory(new RedMushroom(mrContext.context, true, 0, 0));
                            break;
                        case 0x04:
                            factory = _328DB4_FactoryFactory(new Flower(mrContext.context, 0, 0));
                            break;
                        case 0x05:
                            factory = (point) -> null;
                            break;
                        case 0x10:
                            factory = _328DB4_FactoryFactory(new PurpleMushroom(mrContext.context, true, 0, 0));
                            break;
                        case 0x11:
                            factory = _328DB4_FactoryFactory(new Goomba(mrContext.context, 0, 0));
                            break;
                        case 0x12:
                            throw new RuntimeException("koppa not implemented");
                        case 0x13:
                            throw new RuntimeException("beetle not implemented");
                        case 0x14:
                            factory = _328DB4_FactoryFactory(new Spiny(mrContext.context, 0, 0));
                            break;
                        default:
                            throw new BadFormatException();
                    }
                    //endregion

                    output.blocks.add(new QuestionBlock(mrContext.context, x, y, factory, mrContext.addEntityLater, appearance));
                    break;
                }
                case 0x03: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    int w = fis.read();
                    int h = fis.read();
                    if (w == -1 || h == -1) throw new BadFormatException();
                    output.blocks.add(new GroundBlock(mrContext.context, x, y, w, h));
                    break;
                }
                case 0x04: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    int w = fis.read();
                    int h = fis.read();
                    if (w == -1 || h == -1) throw new BadFormatException();
                    output.blocks.add(new SolidBlock(mrContext.context, x, y, w, h));
                    break;
                }
                case 0x05: {
                    throw new RuntimeException("bullet bill not implemented");
                }
                case 0x06: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.blocks.add(new ShinyFragileBlock(mrContext.context, x, y));
                    break;
                }
                case 0x10: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new Goomba(mrContext.context, x, y));
                    break;
                }
                case 0x11: {
                    throw new RuntimeException("green koppa not implemented");
                }
                case 0x12: {
                    throw new RuntimeException("beetle not implemented");
                }
                case 0x13: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    int speedRaw = fis.read();
                    if (speedRaw == -1) throw new BadFormatException();
                    int speedRefined = (speedRaw & 0x80) == 0 ? speedRaw : speedRaw - 255;
                    output.enemies.add(new Bullet(mrContext.context, x, y, speedRefined));
                    break;
                }
                case 0x20: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new RedMushroom(mrContext.context, true, x, y));
                    break;
                }
                case 0x21: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new RedMushroom(mrContext.context, false, x, y));
                    break;
                }
                case 0x22: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new GreenMushroom(mrContext.context, true, x, y, mrContext.lifeIncrement));
                    break;
                }
                case 0x23: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new GreenMushroom(mrContext.context, false, x, y, mrContext.lifeIncrement));
                    break;
                }
                case 0x24: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new PurpleMushroom(mrContext.context, true, x, y));
                    break;
                }
                case 0x25: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new PurpleMushroom(mrContext.context, false, x, y));
                    break;
                }
                case 0x26: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    output.enemies.add(new Flower(mrContext.context, x, y));
                    break;
                }
                case 0x27: {
                    throw new RuntimeException("not implemented");
                }
                case 0x30: {
                    int x = readShort(fis);
                    int y = readShort(fis);
                    chkpointBuffer.add(new Point2D.Double(x, y));
                    break;
                }
                default: {
                    throw new BadFormatException();
                }
            }
        }

        if (!normal) throw new BadFormatException();
        output.checkpointXs = new double[chkpointBuffer.size()];
        output.checkpointYs = new double[chkpointBuffer.size()];
        for (int i = 0; i < output.checkpointXs.length; i++) {
            output.checkpointXs[i] = chkpointBuffer.get(i).x;
            output.checkpointYs[i] = chkpointBuffer.get(i).y;
        }
        return output;
    }
}
