package cn.ussshenzhou.madparticle.command;

import cn.ussshenzhou.madparticle.command.inheritable.*;
import cn.ussshenzhou.madparticle.network.MadParticlePacket;
import cn.ussshenzhou.madparticle.particle.ChangeMode;
import cn.ussshenzhou.madparticle.particle.MadParticleOption;
import cn.ussshenzhou.madparticle.particle.ParticleRenderTypes;
import cn.ussshenzhou.madparticle.particle.SpriteFrom;
import cn.ussshenzhou.t88.network.NetworkHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * @author USS_Shenzhou
 */
public class MadParticleCommandTeaCon {
    private static final int COMMAND_LENGTH = 40;

    public MadParticleCommandTeaCon(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("mp_demo")
                        //.requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.argument("targetParticle", ParticleArgument.particle(Commands.createValidationContext(VanillaRegistries.createLookup())))
                                .then(Commands.argument("spriteFrom", EnumArgument.enumArgument(SpriteFrom.class))
                                        .then(Commands.argument("lifeTime", new InheritableIntegerArgument(0, 100, COMMAND_LENGTH))
                                                .then(Commands.argument("alwaysRender", EnumArgument.enumArgument(InheritableBoolean.class))
                                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10))
                                                                .then(Commands.argument("spawnPos", InheritableVec3Argument.inheritableVec3(COMMAND_LENGTH))
                                                                        .then(Commands.argument("spawnDiffuse", Vec3Argument.vec3(false))
                                                                                .then(Commands.argument("spawnSpeed", InheritableVec3Argument.inheritableVec3(COMMAND_LENGTH))
                                                                                        .then(Commands.argument("speedDiffuse", Vec3Argument.vec3(false))
                                                                                                .then(Commands.argument("collision", EnumArgument.enumArgument(InheritableBoolean.class))
                                                                                                        .then(Commands.argument("bounceTime", InheritableIntegerArgument.inheritableInteger(0, 3, COMMAND_LENGTH))
                                                                                                                .then(Commands.argument("horizontalRelativeCollisionDiffuse", new InheritableDoubleArgument(-2, 2, COMMAND_LENGTH))
                                                                                                                        .then(Commands.argument("verticalRelativeCollisionBounce", new InheritableDoubleArgument(-2, 2, COMMAND_LENGTH))
                                                                                                                                .then(Commands.argument("friction", FloatArgumentType.floatArg(0, 1))
                                                                                                                                        .then(Commands.argument("afterCollisionFriction", FloatArgumentType.floatArg(0, 1))
                                                                                                                                                .then(Commands.argument("gravity", FloatArgumentType.floatArg(-0.5f, 0.5f))
                                                                                                                                                        .then(Commands.argument("afterCollisionGravity", FloatArgumentType.floatArg(-0.5f, 0.5f))
                                                                                                                                                                .then(Commands.argument("xDeflection", FloatArgumentType.floatArg(-0.5f, 0.5f))
                                                                                                                                                                        .then(Commands.argument("xDeflectionAfterCollision", FloatArgumentType.floatArg(-0.5f, 0.5f))
                                                                                                                                                                                .then(Commands.argument("zDeflection", FloatArgumentType.floatArg(-0.5f, 0.5f))
                                                                                                                                                                                        .then(Commands.argument("zDeflectionAfterCollision", FloatArgumentType.floatArg(-0.5f, 0.5f))
                                                                                                                                                                                                .then(Commands.argument("rollSpeed", InheritableFloatArgument.inheritableFloat(-0.5f, 0.5f))
                                                                                                                                                                                                        .then(Commands.argument("interactWithEntity", EnumArgument.enumArgument(InheritableBoolean.class))
                                                                                                                                                                                                                .then(Commands.argument("horizontalInteractFactor", new InheritableDoubleArgument(-2, 2, COMMAND_LENGTH))
                                                                                                                                                                                                                        .then(Commands.argument("verticalInteractFactor", new InheritableDoubleArgument(-2, 2, COMMAND_LENGTH))
                                                                                                                                                                                                                                .then(Commands.argument("renderType", EnumArgument.enumArgument(ParticleRenderTypes.class))
                                                                                                                                                                                                                                        .then(Commands.argument("r", new InheritableFloatArgument(0, 10, COMMAND_LENGTH))
                                                                                                                                                                                                                                                .then(Commands.argument("g", new InheritableFloatArgument(0, 10, COMMAND_LENGTH))
                                                                                                                                                                                                                                                        .then(Commands.argument("b", new InheritableFloatArgument(0, 10, COMMAND_LENGTH))
                                                                                                                                                                                                                                                                .then(Commands.argument("bloomFactor", new InheritableFloatArgument(0, 1, COMMAND_LENGTH))
                                                                                                                                                                                                                                                                        .then(Commands.argument("beginAlpha", FloatArgumentType.floatArg(0, 1))
                                                                                                                                                                                                                                                                                .then(Commands.argument("endAlpha", FloatArgumentType.floatArg(0, 1))
                                                                                                                                                                                                                                                                                        .then(Commands.argument("alphaMode", EnumArgument.enumArgument(ChangeMode.class))
                                                                                                                                                                                                                                                                                                .then(Commands.argument("beginScale", FloatArgumentType.floatArg(0, 5))
                                                                                                                                                                                                                                                                                                        .then(Commands.argument("endScale", FloatArgumentType.floatArg(0, 5))
                                                                                                                                                                                                                                                                                                                .then(Commands.argument("scaleMode", EnumArgument.enumArgument(ChangeMode.class))
                                                                                                                                                                                                                                                                                                                        .executes(ct1 -> sendToAll(ct1, dispatcher))
                                                                                                                                                                                                                                                                                                                        .then(Commands.argument("whoCanSee", EntityArgument.players())
                                                                                                                                                                                                                                                                                                                                .executes((ct) -> sendToPlayer(ct, EntityArgument.getPlayers(ct, "whoCanSee"), dispatcher))
                                                                                                                                                                                                                                                                                                                                .then(Commands.argument("meta", CompoundTagArgument.compoundTag())
                                                                                                                                                                                                                                                                                                                                        .then(Commands.literal("expireThen")
                                                                                                                                                                                                                                                                                                                                                .redirect(dispatcher.register(Commands.literal("mp")))
                                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                                                                                                .then(Commands.literal("expireThen")
                                                                                                                                                                                                                                                                                                                                        .redirect(dispatcher.register(Commands.literal("mp")))
                                                                                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                )
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                )
                                                                                                                                                                                                        )
                                                                                                                                                                                                )
                                                                                                                                                                                        )
                                                                                                                                                                                )
                                                                                                                                                                        )
                                                                                                                                                                )
                                                                                                                                                        )
                                                                                                                                                )
                                                                                                                                        )
                                                                                                                                )
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int sendToAll(CommandContext<CommandSourceStack> ct, CommandDispatcher<CommandSourceStack> dispatcher) {
        return sendToPlayer(ct, ct.getSource().getLevel().getPlayers(serverPlayer -> true), dispatcher);
    }

    private static int sendToPlayer(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, CommandDispatcher<CommandSourceStack> dispatcher) {
        CompletableFuture.runAsync(() -> {
            send(context.getInput(), context.getSource(), players, dispatcher);
        });
        return Command.SINGLE_SUCCESS;
    }

    private static boolean sendTada = false;

    private static void initializeFlags() {
        sendTada = false;
    }

    public static void send(String commandString, CommandSourceStack sourceStack, Collection<ServerPlayer> players, CommandDispatcher<CommandSourceStack> dispatcher) {
        initializeFlags();
        String[] commandStrings = commandString.split(" expireThen ");
        MadParticleOption child = null;
        for (int i = commandStrings.length - 1; i >= 0; i--) {
            String s = commandStrings[i];
            if (s.startsWith("/")) {
                s = s.replaceFirst("/", "");
            }

            if (!s.startsWith("mp") && !s.startsWith("madparticle")) {
                if (!s.startsWith("execute")) {
                    s = "mp " + s;
                    s = s.replaceFirst("madparticle", "mp");
                }
            }
            InheritableCommandDispatcher<CommandSourceStack> inheritableCommandDispatcher = new InheritableCommandDispatcher<>(dispatcher.getRoot());
            ParseResults<CommandSourceStack> parseResults = inheritableCommandDispatcher.parse(new InheritableStringReader(s), sourceStack);
            CommandContext<CommandSourceStack> ctRoot = parseResults.getContext().build(s);
            CommandContext<CommandSourceStack> ct = CommandHelper.getContextHasArgument(ctRoot, "targetParticle", ParticleOptions.class);

            try {
                CompoundTag metaTag = ct.getArgument("meta", CompoundTag.class);
                if (metaTag.getBoolean("tada")) {
                    sendTada = true;
                }
            } catch (IllegalArgumentException ignored) {
            }

            Vec3 pos = ct.getArgument("spawnPos", Coordinates.class).getPosition(sourceStack);
            Vec3 posDiffuse = ct.getArgument("spawnDiffuse", WorldCoordinates.class).getPosition(sourceStack);

            Vec3 speed = ct.getArgument("spawnSpeed", WorldCoordinates.class).getPosition(sourceStack);
            if (Math.abs(speed.x) > 1 || Math.abs(speed.y) > 1 || Math.abs(speed.z) > 1) {
                return;
            }
            Vec3 speedDiffuse = ct.getArgument("speedDiffuse", WorldCoordinates.class).getPosition(sourceStack);
            if (Math.abs(speedDiffuse.x) > 1 || Math.abs(speedDiffuse.y) > 1 || Math.abs(speedDiffuse.z) > 1) {
                return;
            }

            boolean haveChild = i != commandStrings.length - 1;
            MadParticleOption father = new MadParticleOption(
                    BuiltInRegistries.PARTICLE_TYPE.getId(ct.getArgument("targetParticle", ParticleOptions.class).getType()),
                    ct.getArgument("spriteFrom", SpriteFrom.class),
                    ct.getArgument("lifeTime", Integer.class),
                    ct.getArgument("alwaysRender", InheritableBoolean.class),
                    ct.getArgument("amount", Integer.class),
                    pos.x, pos.y, pos.z,
                    posDiffuse.x, posDiffuse.y, posDiffuse.z,
                    speed.x, speed.y, speed.z,
                    speedDiffuse.x, speedDiffuse.y, speedDiffuse.z,
                    ct.getArgument("friction", Float.class),
                    ct.getArgument("gravity", Float.class),
                    ct.getArgument("collision", InheritableBoolean.class),
                    ct.getArgument("bounceTime", Integer.class),
                    ct.getArgument("horizontalRelativeCollisionDiffuse", Double.class),
                    ct.getArgument("verticalRelativeCollisionBounce", Double.class),
                    ct.getArgument("afterCollisionFriction", Float.class),
                    ct.getArgument("afterCollisionGravity", Float.class),
                    ct.getArgument("interactWithEntity", InheritableBoolean.class),
                    ct.getArgument("horizontalInteractFactor", Double.class),
                    ct.getArgument("verticalInteractFactor", Double.class),
                    ct.getArgument("renderType", ParticleRenderTypes.class),
                    ct.getArgument("r", Float.class), ct.getArgument("g", Float.class), ct.getArgument("b", Float.class),
                    ct.getArgument("beginAlpha", Float.class),
                    ct.getArgument("endAlpha", Float.class),
                    ct.getArgument("alphaMode", ChangeMode.class),
                    ct.getArgument("beginScale", Float.class),
                    ct.getArgument("endScale", Float.class),
                    ct.getArgument("scaleMode", ChangeMode.class),
                    haveChild,
                    haveChild ? child : null,
                    ct.getArgument("rollSpeed", Float.class),
                    ct.getArgument("xDeflection", Float.class),
                    ct.getArgument("xDeflectionAfterCollision", Float.class),
                    ct.getArgument("zDeflection", Float.class),
                    ct.getArgument("zDeflectionAfterCollision", Float.class),
                    ct.getArgument("bloomFactor", Float.class)
            );
            child = father;
        }
        for (ServerPlayer player : players) {
            NetworkHelper.getChannel(MadParticlePacket.class).send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new MadParticlePacket(child)
            );
        }
    }

    public static void fastSend(String commandString, CommandSourceStack sourceStack, Collection<ServerPlayer> players, CommandDispatcher<CommandSourceStack> dispatcher) {
        InheritableCommandDispatcher<CommandSourceStack> inheritableCommandDispatcher = new InheritableCommandDispatcher<>(dispatcher.getRoot());
        ParseResults<CommandSourceStack> parseResults = inheritableCommandDispatcher.parse(new InheritableStringReader(commandString), sourceStack);
        CommandContext<CommandSourceStack> ctPre = parseResults.getContext().build(commandString);
        CommandContext<CommandSourceStack> ctExe = CommandHelper.getContextHasArgument(ctPre, "targets", EntitySelector.class);
        if (ctExe != null) {
            try {
                Collection<? extends Entity> entities = EntityArgument.getOptionalEntities(ctExe, "targets");
                for (Entity entity : entities) {
                    CompletableFuture.runAsync(() -> send(commandString, ctPre.getSource().withEntity(entity).withPosition(entity.position()), players, dispatcher));
                }
            } catch (CommandSyntaxException ignored) {
            }
        } else {
            send(commandString, sourceStack, players, dispatcher);
        }
    }

    public static ParseResults<CommandSourceStack> justParse(String commandText) {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        new MadParticleCommandTeaCon(dispatcher);
        InheritableCommandDispatcher<CommandSourceStack> inheritableDispatcher = new InheritableCommandDispatcher<>(dispatcher.getRoot());
        CommandSourceStack sourceStack = Minecraft.getInstance().player.createCommandSourceStack();
        return inheritableDispatcher.parse(new InheritableStringReader(commandText), sourceStack);
    }
}
