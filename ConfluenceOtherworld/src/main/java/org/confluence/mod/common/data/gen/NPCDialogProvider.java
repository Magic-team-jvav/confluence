package org.confluence.mod.common.data.gen;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.entity.npc.dialog.NPCDialog;
import org.confluence.mod.common.init.entity.NpcEntities;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/// 统一管理城镇 NPC 的运行时对话表与双语文本。
public final class NPCDialogProvider implements DataProvider {
    // 每个 NPC 的普通对话与具名文本集中在同一块；普通对话的键和序号由实体注册名自动生成。
    private static void gather(BiConsumer<RegistryObject<? extends EntityType<?>>, NPCDefinition> consumer) {
        consumer.accept(NpcEntities.GUIDE, npc()
                .dialog("My job is to offer suggestions for your upcoming tasks. I recommend that you come and talk to me whenever you encounter any difficulties.", "我的工作是为你接下来的任务提供建议。建议你遇到任何困难时都来和我谈谈。")
                .dialog("They said there would be someone to tell you how to survive in this place... Oh, wait a moment. That person is me.", "他们说，有个人会告诉你如何在这地方上生存……哦等下。那个人就是我。")
                .dialog("You should stay at home at night. It's very dangerous to wander outside in the dark.", "晚上你应该呆在家里。黑夜在外面转悠非常危险。")
                .dialog("In the Confluence world, you will obtain multiple times the treasure, but this also means taking on multiple times the risk.", "在融合的世界中，你会收获多倍的宝藏，但这也以为着承担多倍的风险。")
                .dialog("As far as I know, there are more humans in this world than in our original world.", "据我所知这个世界上的人类比我们原来的世界更多。")
                .dialog("Sorry, sometimes I have to open the door.", "抱歉，有时候我不得不开门。")
                .dialog("Those guys that can explode are more threatening than the average surface monsters!", "那些会爆炸的家伙比一般的地表怪物更具威胁！")
                .dialog("The life mushrooms on the grass can sometimes save your life.", "草地上的生命蘑菇有时候可以救你一命。")
                .dialog("There are Crystal Hearts underground, which can be used to increase your maximum health. You can use a pickaxe to break them.", "地下有水晶之心，可以用来提高你的最大生命值。你可以用镐来打碎它们。")
                .dialog("There is a lake with magical powers underground, and it's very rare.", "地底下有一种具有神奇魔力的湖，它非常稀有。")
                .dialog("At night, stars are falling and spreading all over the world. They have extremely wide uses. If you see them, you must get them, because the stars will disappear after sunrise.", "夜晚，星星在坠落，洒满全世界。它们的用途极为广泛。如果你看到了，一定要拿到手，因为星星在日出后就会消失。")
                .text("jei_check", "In Minecraft, I can't help you look up recipes, but I know a mod called JEI that can help you.", "在Minecraft里，我帮不到你查询配方，但是我知道一个叫JEI的模组可以帮到你。")
                .build());
        consumer.accept(NpcEntities.MERCHANT, npc()
                .dialog("Swords beat paper! Buy one right away.", "剑克纸！赶紧买一把。")
                .dialog("Do you want apples? Do you want carrots? Do you want pineapples? All we have are torches.", "你想要苹果？你想要胡萝卜？你想要菠萝？我们只有火把。")
                .dialog("Take a look at my dirt blocks; they're really earthy.", "看看我的土块；它们特别土。")
                .dialog("You have no idea how much dirt blocks can sell for in other places.", "你是不知道土块能在国外卖多少钱。")
                .dialog("One day they will tell your legend... It's sure to be a good story.", "总有一天他们会讲述你的传奇……肯定会是好故事。")
                .dialog("Kosh, kapleck Mog. Oh, sorry, that's Klingon, which means 'Buy or die.'", "Kosh, kapleck Mog。哦，对不起，这是克林贡语，意思是“要么买，要么死。”")
                .build());
        consumer.accept(NpcEntities.NURSE, npc()
                .dialog("I need to have a serious talk with the Guide. How many times a week do you get severely burned by lava exactly?", "我要和向导认真谈一谈。你一周到底有多少次被熔岩烫成重伤？")
                .dialog("See that old man wandering around the dungeon? He looks like he's in trouble.", "看到那个在地牢周围转来转去的老人没？他看上去遇到麻烦了。")
                .dialog("Hey, has the Arms Dealer ever mentioned going to see a doctor or something? Just asking.", "嗨，军火商有没有提过要去看医生啥的？就随便问问。")
                .dialog("Got into trouble with the thugs again?", "又惹上混混了？")
                .dialog("Don't be such a child! I've seen worse.", "别像个孩子似的！我见过更糟的。")
                .dialog("Did it hurt when you did that? Don't do that.", "你这么做的时候疼吗？别那么做。")
                .text("player_killed_by", "Have you been killed %2$s times by %1$s? I'm curious who took your flag", "你已经被%1$s杀死%2$s次了？我很好奇是谁拿走了你的旗子。")
                .build());
        consumer.accept(NpcEntities.DEMOLITIONIST, npc()
                .dialog("Explosives are really popular nowadays. Buy some right away!", "炸药如今十分火爆。马上买一些！")
                .dialog("Today is a great day to court death!", "今天是个找死的好日子！")
                .dialog("Let me see what happens if I do this... (BOOM!)... Oh, sorry, did you still need that leg?", "让我看看这样会怎……（轰！）……哦，对不起，你还要那条腿吗？")
                .dialog("Take a look at my goods; they're all at amazing prices.", "看看我的商品；都是惊爆价")
                .dialog("Dynamite, this is my special panacea prepared just for you. It can cure all kinds of problems.", "雷管，这是我特别为你准备的灵丹妙药，包治百病。")
                .dialog("Want to get through those evil stones, huh? Why not just blow them up with explosives!", "想穿过那些邪恶石头，嗯？为什么不用炸药炸掉它！")
                .build());
        consumer.accept(NpcEntities.DYE_TRADER, npc()
                .dialog("I bring you the richest colors in exchange for your wealth.", "我带给你最丰富的色彩，以换取你的财富。")
                .dialog("Honey, your clothes are so monotonous. You really have to learn how to dye your dull clothes!", "亲爱的，你的穿着太单调了。你一定得好好学学，怎么给单调的衣服染色！")
                .dialog("The only wood I'm willing to dye is mahogany. Dyeing any other wood is a waste.", "我唯一愿意染的木材是红木。给任何其他木材染色都是浪费。")
                .dialog("Oh, no, no, that won't do. Even if you have money, you have to trade me with rare plant samples!", "噢，不行，不行，这样是不行的。有钱也没用，你必须拿稀有的植物样本来和我交换！")
                .dialog("These dye bottles? Sorry, my dear friend, these are not for sale. I only accept the rarest plants in exchange for them!", "这些染料瓶？抱歉，亲爱的朋友，这些是非卖品。我只接受用最珍稀的植物来交换它们！")
                .dialog("You think you can fool my eyes? I don't think so! I only accept the rarest flowers in exchange for these special bottles.", "你以为可以骗过我的眼睛？我可不这么想！我只接受用最稀有的花来交换这些特别的瓶子。")
                .build());
        consumer.accept(NpcEntities.ANGLER, npc()
                .dialog("I'm bummed out! There's probably been fish that have gone extinct before I even was born, and that's not fair!", "太可气了！有些鱼可能在我出生之前就灭绝了，真不公平！")
                .dialog("Whaaaat?! Can't you see I'm winding up fishing line??", "什……么？！难道你没看见我在收钓鱼线吗？？")
                .dialog("There's no chefs in all of %s, so I have to cook all this fish myself! ", "整个%s中都没有厨师，所以我不得不自己烹鱼！")
                .dialog("I don't have a mommy or a daddy, but I have a lot of fish! It's close enough!", "我没有妈妈，也没有爸爸，但我有很多鱼！这就够了！")
                .dialog("Let a kid give you some advice, never touch your tongue to an ice block! Wait, forget what I said, I totally want to see you do it!", "听听小孩的忠告吧，永远不要用舌头碰冰块！等一下，就当我没说，我就想看你这样做！")
                .dialog("Ever heard of a barking fish?! I haven't, I'm just wondering if you did!", "听说过会叫的鱼吗？！我没听说过，只是想知道你听说过没！")
                .text("wakeup.0", "Thanks, I guess, for saving me or whatever. You'd be a great helper minion!", "谢谢，我想，谢谢你救了我之类的。你是个优秀的得力仆从！")
                .text("wakeup.1", "Wha? Who might you be? I totally wasn't just drowning or anything!", "啥？你是哪位？我绝对不是溺水之类的！")
                .text("wakeup.2", "You saved me! You're awful nice, I could use you... er, I mean, totally hire you to do some awesome stuff for me!", "你救了我！你太好了，我可以使唤你……呃，我是说，雇你帮我做些了不起的事！")
                .text("completed", "You've already finished today's fishing quest. Come back tomorrow!", "你已经完成今天的钓鱼任务了，明天再来！")
                .text("no_quest", "There is no fishing quest available right now.", "现在没有可接取的钓鱼任务。")
                .text("quest_fish", "Today's quest fish is %s.", "今天的任务鱼是%s。")
                .text("item.confluence.amanita_fungifin", "I found this spectacular place draped in giant glowing mushrooms! Everything was blue! I was picking some of the mushrooms I found next to a glistening blue lake, when one of the mushrooms snapped at me and swam away! I want to give it a taste of its own medicine, and give it a good chompin'! What I mean is, you gotta get it for me!(Caught in Glowing Mushroom Biomes)", "我在巨大的发光蘑菇中发现了这个惊人的地方！一切都是蓝的！我正在采摘蓝光湖畔的一些蘑菇，其中一只蘑菇突然咬了我一口，然后游走了！我想以其人之道还治其人之身，并狂咬它一顿！我的意思是，你去把它弄回来给我！（抓捕位置：发光蘑菇地）")
                .text("item.confluence.angelfish", "Did you know there's magical islands that float up high in the sky? Bet you didn't! They say angels live in the sky, and I believe those angels have fins and gills and swim around! I believe you must catch one for me!(Caught in Sky Lakes)", "你知道吗，天空中漂浮着许多神奇的岛屿？你肯定不知道！他们说，天使住在天上，我相信这些天使都有鳍和鳃，在那游来游去！我相信你肯定能抓一个给我！（抓捕位置：天湖）")
                .text("item.confluence.batfish", "Na na na na na na na Bat-FISH! That means go digging underground, fetch it, and bring it to me!(Caught in Underground & Caverns)", "呐呐呐呐呐呐呐，蝙蝠鱼！意思是去地下深挖，抓住它，再带来给我！（抓捕位置：地下和洞穴）")
                .text("item.confluence.bloody_manowar", "Ow! Don't get near me! I got stung by a Bloody Man-O-War! In case you're not smart enough to know what that is, it's the most menacing jellyfish in all of world ! Go to that rotten crimson and catch it if you dare! (Caught in Crimson)", "噢！别靠近我！我被一条血水母蜇了！怕你孤陋寡闻不知道血水母是什么，所以我告诉你，它是整个世界中最凶险的水母！如果你敢，去那个烂透了的猩红之地，把它抓来！（抓捕位置：猩红之地）")
                .text("item.confluence.bonefish", "Normally I could care less if I see fishbones floating in the water underground, but this one was swimming! What, you thought that only human skeletons still flailed about in world ? Get it for me so I can stick it in someone's bed!(Caught in Underground & Caverns)", "如果看到地下水中漂浮着鱼骨，我通常不怎么好奇，但是这只鱼骨竟然在游泳！什么，你以为在世界中还能动的只有人类的骷髅吗？去抓一只给我，这样我可以把它钉在别人的床上！（抓捕位置：地下和洞穴）")
                .text("item.confluence.bumblebee_tuna", "The subterranean jungles of world have the weirdest things! Like, there's this fish I saw that looked just like a giant bumblebee! I'm allergic to bees, so you have to catch it for me! I bet it'd taste like a tuna and honey sandwich!(Caught in Honey)", "在世界的地下丛林中有许多光怪陆离的生物！比如说，我看见过这种鱼，像一只超级大黄蜂！我对蜜蜂过敏，所以你必须去把它给我抓来！它的味道肯定像金枪鱼和蜂蜜三明治！（抓捕位置：蜂蜜）")
                .text("item.confluence.bunnyfish", "Out in the forest, I was fishing right? Well guess what! A bunny hopped up to me! Then another one hopped up, and another... suddenly I'm surrounded by bunnies! One even swam up to me from the water, but it had no legs! I fell outta my chair in surprise and all the bunnies scurried off! I want that bunny fish as a pet, so you better catch it for me! Pronto!(Caught in Surface)", "我正在森林里钓鱼，对吧？你猜发生了什么！一只兔兔朝我跳了过来！然后，又有一只跳过来，又有一只……突然间我就被兔兔包围了！甚至有一只从水里向我游过来，但它没有腿！我惊讶地从椅子上跌下来，把所有兔兔都吓跑了！我想要那条兔兔鱼作为宠物，因此你最好去把它抓来给我！马上！（抓捕位置：地表）")
                .text("item.confluence.capn_tunabeard", "Yarr matey! Shiver me timbers! Avast, scallywags! There's a pirate captain who once had a pet fish named Cap'n Tunabeard, but during a big storm the fishbowl fell overboard! It has a hook for a tail, and an eyepatch and everything! You need to fetch me that fish, so I can be as cool as a pirate! Obviously it's out in the ocean somewhere! Duh!(Caught in Ocean)", "哟，朋友！吓本大盗一跳！停船，我的乖乖！有一个海盗船长，养了一条叫“金枪鱼须船长”的宠物鱼，但在一场大风暴中，鱼缸落水了！它有一个尾钩，一个眼罩，还有别的！你需要把那条鱼抓给我，这样我就能像海盗一样酷酷的！显然，它就在大海的某个地方！咄！（抓捕位置：海洋）")
                .text("item.confluence.catfish", "I finally found a jungle cat that likes water! I think it's because it's also part fish. I don't know why this happened, and I don't want to know. I just want it in my hands, and make it snappy!(Caught in Jungle Surface)", "我终于找到一只喜欢水的丛林猫！我想这是因为它还有鱼的天性。我不知道为什么会这样，我也不想知道。我只是希望它能到我手里，你得快点！（抓捕位置：丛林地表）")
                .text("item.confluence.cloudfish", "There's a rumor going around that there are islands that float high up in the sky, and that they have amazing treasure! Who cares about that though, what's even cooler is that sometimes lakes form in the clouds, and in those lakes are fish made out of clouds! I wanna know what it tastes like, so you better go catch it for me!(Caught in Sky Lakes)", "有谣言说天上漂浮着岛屿，岛上有惊人的宝藏！但是谁会在意呢，更有意思的是，云彩中有时会形成湖泊，湖泊中游荡着云朵做成的鱼！我想尝尝它的味道，所以你最好去把它抓来给我！（抓捕位置：天湖）")
                .text("item.confluence.clownfish", "I saw this bright orange and colorful fish by the ocean, and it was looking around frantically as though it was seeking a lost family member! Go catch it for me, so that another one will show up looking frantically for him instead!(Caught in Ocean)", "我在海边看到过这条亮橙色的彩鱼，它在疯狂地游来游去，好像在找走丢的家人！去把它抓来给我，这样又会有一条游出来找它！（抓捕位置：海洋）")
                .text("item.confluence.cursedfish", "There's a cursed fish swimming in the waters of the deepest corruption! It was forged of the cursed flames that spread from the fallen horrors that lurk down there. They say not even water can put out this fire, and that it can burn forever. I can think of some awesome things I can do with a fish like that! You gonna go get it for me, or are you too chicken!?(Caught in Corruption)", "有条受诅咒的鱼在腐化之地最深处的水中游来游去！它由潜藏在那里的堕落恐怖蔓延出来的诅咒焰化成。他们说，诅咒焰连水都无法扑灭，可以永远燃烧。有一条这样的鱼，我就可以做许多好玩的事！你去抓给我！还是你太胆小！？（抓捕位置：腐化之地）")
                .text("item.confluence.demonic_hellfish", "I hear in the underworld, that the King of all demons is actually a fish! Just imagine the absolute power I would have if you caught it for me!(Caught in Caverns)", "我听说在地狱，所有恶魔的王其实是一条鱼！想象一下，如果你去抓一条给我，我将拥有强大的王权！（抓捕位置：洞穴）")
                .text("item.confluence.derpfish", "Those Derplings in the jungle are the most scary creatures I've ever seen! Good thing is, sometimes they don't have legs! These ones live in the water and are a lot less scary! Catch me one now so I can see what they taste like without being scared half to death!(Caught in Jungle Surface)", "丛林里的那些跳跳兽是我见过的最可怕的怪物！好的一面是，有时候它们没有腿！这些是生活在水中的，就没那么可怕了！马上去给我抓一只，这样我可以品尝它们的味道，还不会被吓个半死！（抓捕位置：丛林地表）")
                .text("item.confluence.dirtfish", "I was reeling in the big one when this funny talking zombie burst out of the forest lake and started rambling on about this 'ferocious' species of fish made out of dirt! He says it could suffocate ten blokes his size, or something like that... I want it! NOW!(Caught in Surface & Underground)", "正当我在收线钓起一条大鱼时，这个僵尸从森林湖中蹦出来，不仅很搞笑还会说话，然后开始大说特说这种用土做成的“凶残”的鱼！他说，这鱼能闷死十个像他这个块头的小伙子……我要得到它！马上！（抓捕位置：地表和地下）")
                .text("item.confluence.dynamite_fish", "The demolitionist was raising cane about losing a stick of dynamite in the lake out in the forest. He has like, so many, so why does one matter? Apparently, because it grew fins and started swimming away! I don't know where he gets his materials to make those things, but that one is clearly possessed! Reel it in and bring it to me, I always wanted a suicide bombing fish! Don't ask why...(Caught in Surface)", "爆破专家在森林里的湖中弄丢了一捆雷管，一直很着急。他有那么多炸药，丢的那捆炸药有这么重要吗？显然，因为这捆炸药长出了鳍，开始游走了！我不知道他做炸药的材料是哪儿弄的，但那一捆肯定已经走火入魔了！钓回来给我，我一直想要一条自杀式炸弹鱼！不要问为什么……（抓捕位置：地表）")
                .text("item.confluence.eater_of_plankton", "I bet you're not brave enough to find the Eater of Plankton. A corrupt fish that was mutated from a severed piece of the Eater of Worlds itself! Capture it and bring it to me, and prove to me you're not a wuss!(Caught in Corruption)", "你肯定不敢去找浮游噬鱼。它是由世界吞噬怪的残块变异而来的腐化鱼！把它抓来给我，证明你不是胆小鬼！（抓捕位置：腐化之地）")
                .text("item.confluence.fallen_starfish", "I love collecting those bright yellow stars that fall from the sky! I love it even more when they land on someone's head. But.. but.. nothing beats a star that falls in a foresty lake and turns into a fish! That's just totally rad, and you're just rad enough to get it for me!(Caught in Sky Lakes & Surface)", "我喜欢收集天上落下来的亮黄色星星！如果落在某个人的脑袋上，我会更喜欢。但是……但是……我最喜欢的还是星星落在森林湖中变成鱼！那条鱼简直酷毙了，而你又这么牛，快去抓来给我！（抓捕位置：天湖和地表）")
                .text("item.confluence.the_fish_of_cthulhu", "Apparently, Demon Eyes can sometimes be amphibious. They don't fly, they swim! I want to see the look on someone's face when they find it in their bathtub! They hang around the same areas. That means you reel one in for me!(Caught in Sky Lakes & Surface)", "显然，恶魔眼有时是两栖的。它们不飞，它们游！我想知道有人在浴缸中发现一条时会有怎样的表情！它们总是在同一个区域晃悠。所以，你要钓一条给我！（抓捕位置：天湖和地表）")
                .text("item.confluence.fishotron", "I don't know what's worse, a bone fish or a bone fish with HANDS. This Fish-o-Tron deep in the caverns really freaks me out! I think it's possessed by the same evil spirits that possessed that old man by the dungeon! I double duck dare you to go catch it!(Caught in Caverns)", "我不知道哪种情况更惨：骷髅鱼还是长了手的骷髅鱼。这条深藏在洞穴中的骷髅王鱼真把我吓了一跳！我认为它和地牢边的那个老人被同一个恶魔掌控着！我给你两个胆，你去把它抓来给我！（抓捕位置：洞穴）")
                .text("item.confluence.fishron", "There's a legend of a mighty being known as the Fishron! It's part pig, part dragon, and part FISH! I hear it hangs around in the frozen subterranean lakes of the coldest part of the world! I'm not going there, so YOU go catch it and makes sure it lands in my hands! I'm so excited!(Caught in Underground Tundra)", "有一个名为猪龙鱼的传奇生物！它一部分是猪，一部分是龙，还有一部分是鱼！我听说，它在世界最寒冷的冰封地下湖中游荡！我不会去那里，因此你去抓它，一定要到我的手里！我太激动了！")
                .text("item.confluence.guide_voodoo_fish", "Those demons in the underworld really like voodoo dolls, but I think there's a doll out there who was blasted with way too much magic! It turned into a fish and it does stuff on its own. I dare you to go down and get me one! I'd watch out for the boiling lava, because it burns you to death and that won't get me my fish!(Caught in Caverns)", "地狱的恶魔真的很喜欢巫毒娃娃，但我觉得有一个娃娃身藏着特别多的魔法！它变成了一条鱼，还可以对自己施法。我命令你去地狱，给我带一个来！如果是我的话，我会小心沸腾的熔岩，因为它会把你烧死，这样我就得不到鱼了！（抓捕位置：洞穴）")
                .text("item.confluence.harpyfish", "I was trying to sleep by the hill lakeside when this fish swooped down at me. It was flying! It also had the face of a lady and had feathers! I think I screamed louder than she did! Hey you, go make her pay for scaring me like that!(Caught in Sky Lakes & Surface)", "我正要在山上的湖畔睡觉时，这条鱼向我俯冲下来。它在飞！它长着一张女人的脸，还有羽毛！我想我叫得比她还大声！嘿，她把我吓成那样，你去让她付出代价！（抓捕位置：天湖和地表）")
                .text("item.confluence.hungerfish", "There's a piece of the Hunger that morphed from the Wall of Flesh into a small fish-like thing that swims around aimlessly in the underworld and it's gross and it's yucky and I want it now!(Caught in Caverns)", "血肉墙上饿鬼的一块碎片变形成了小鱼一样的东西，在地狱漫无目的地游来游去。它很恶心，但我现在就要！（抓捕位置：洞穴）")
                .text("item.confluence.ichorfish", "Did you know deep in the crimson, some of those creatures make this gross yellow stuff? I overheard a crazy story about a pool of it having melted together into a shape of a fish and it swims around and everything! Fetch it for me, so I can stick it in someone's toilet!(Caught in Crimson)", "你知道吗？在猩红之地的深处，一些生物在制作这种恶心的黄东西。我听说一池黄东西融合成鱼形，然后开始游来游去，太离奇了！去抓一条给我，我可以把它塞在别人的马桶里！（抓捕位置：猩红之地）")
                .text("item.confluence.infected_scabbardfish", "A really long fish that looks like a sword's sheath swims in the murky waters of the corruption! It looks a lot like ebonstone, so don't let it fool you! That's right, you. You're catching it, not me!(Caught in Corruption)", "一条很长的鱼，看起来像剑鞘，在腐化之地的浑水中游来游去！它看起来很像黑檀石，所以不要让它骗了你！没错，就你啦。你去抓它，而不是我去！（抓捕位置：腐化之地）")
                .text("item.confluence.jewelfish", "Oooooohhh, I'm going to be SO rich! Deep in the caverns, there is a fish made out of gemstones! Don't ask me how, I don't know, all I know is that this fish is totally awesome and you're going to catch it for me!(Caught in Underground & Caverns)", "哦哦，我马上就要发大财啦！在洞穴的深处，有一种宝石做的鱼！别问我怎么做，我不知道，我知道的是，这种鱼非常美丽，你去把它抓来给我！（抓捕位置：地下和洞穴）")
                .text("item.confluence.mirage_fish", "There's some interesting critters to be found in the deeper Hallows, I tell you! They glow this crazy purple color and it messes with my eyes! It's totally wild, so I want you to catch a fish like that for me!(Caught in Underground Hallow)", "我告诉你，在地下更深处的神圣之地里可以找到一些有趣的小动物！它们闪耀着疯狂的紫色，让我眼花缭乱！这鱼是绝对狂野的，因此我希望你能抓一条给我！（抓捕位置：地下神圣之地）")
                .text("item.confluence.mudfish", "Watch your step when wading through jungle waters! Why? No, not because I care about you being eaten by piranhas. I care because you'll step on one of my favorite kinds of fish, the Mud Fish! I also care a lot that you're going to grab me one as a pet!(Caught in Jungle)", "经过丛林的水域时，要注意脚下！为什么？不，我不担心你会被食人鱼吃掉。我担心你会踩到我最喜欢的一种鱼，泥鱼！我还希望，你会抓一条来给我当宠物！（抓捕位置：丛林）")
                .text("item.confluence.mutant_flinxfin", "What's white and tan and fluffy and lives in a frozen underground lake? A mutant flinxfin! I wasn't telling a joke, you know, there really is a mutated variety of Flinx that is more adapted to an aquatic lifestyle! I want it to adapt to my fishbowl, so make sure that happens!(Caught in Underground Tundra)", "棕白色、毛茸茸，住在冰冻地下湖中，是什么鱼？突变雪怪鱼！我没有开玩笑，确实有一种更适应水生生活的突变雪怪鱼！我希望它能够适应我的鱼缸，一定要抓一条！（抓捕位置：地下苔原）")
                .text("item.confluence.pengfish", "It's a whale! It's a dolphin! No, it's a penguin fish! Oh, and look, it's you! You get to bring me one! You do know they only like cold water, right?(Caught in Surface Tundra)", "是鲸鱼！是海豚！不，是企鹅鱼！瞧瞧，该你出马了！你去给我抓一条！你知道它们只喜欢冷水吧？（抓捕位置：地表苔原）")
                .text("item.confluence.pixiefish", "There's a really really rare type of pixie that's born with so many wings that it can't actually fly! It swims with the fishes in the lakes surrounded by that blue colored grass. My fish tank needs a lamp, so I want you to catch me that pixie!(Caught in Surface Hallow)", "有一种十分十分罕见的妖精，长了太多翅膀，所以完全飞不起来！它在蓝色草地环绕的湖中与鱼儿一起游来游去。我的鱼缸需要一盏灯，所以我想让你把那个妖精抓来给我！（抓捕位置：地表神圣之地）")
                .text("item.confluence.scarab_fish", "I read this ancient story about a fish that looks like a magical scarab! That's beetle, for simpletons like you! Where do you find it? Where do you think? In the desert, duh! Don't look at me like that... it's true! There is actually water out there! I'd go, but I don't like having my eyeballs pecked out by vultures. So... you do it!(Caught in Desert)", "我读过这个古老的故事，讲的是一条长得像魔法金龟子的鱼！说白了就是甲虫！在哪里能找到它？你说呢？咄，在沙漠！别那样看着我……是真的！那里其实是有水的！我想去，但我怕我的眼珠子被秃鹰啄掉。所以……还是你去吧！（抓捕位置：沙漠）")
                .text("item.confluence.scorpio_fish", "I'm sure someone, a really stupid someone, tried to tell you there was no water in the desert! They were absolutely and positively wrong! There's this thing called an Oasis, and it has water in it! Guess what happens when you have water!? That's right, you have fish! Weird fish that try to sting you and pinch you and do all kinds of other mean things to you! A perfect pet for me, and a perfect job for you!(Caught in Desert)", "我敢肯定有人告诉过你沙漠里没有水，真是愚蠢至极！大错特错！沙漠里有种叫绿洲的地方，那里就有水！你说有水就会有什么！？没错，有鱼！那些奇怪的鱼会叮你、咬你，用尽各种方法折磨你！这是一种很适合我的宠物，也是一个很适合你的任务！（抓捕位置：沙漠）")
                .text("item.confluence.slimefish", "In the forest, the slimes are kinda gross. Slimefish are even more so! I don't want to swim with slimes, so yoink one out of the water for me!(Caught in Surface Forest)", "森林里的史莱姆有点恶心。史莱姆鱼更恶心！我可不想与史莱姆们一起游泳，所以你快去从水里抓一条给我！（抓捕位置：地表森林）")
                .text("item.confluence.spiderfish", "I saw a fish that had eight legs! Nope! Not happening! You're fishing it for me, so it's not alive when I hold it! That's the last time I go fishing so deep in the cavern!(Caught in Underground & Caverns)", "我看到一条八条腿的鱼！不！不可能！你为我钓它，这样它到我手里时就是死的了！这是我最后一次去这么深的洞穴钓鱼！（抓捕位置：地下和洞穴）")
                .text("item.confluence.tropical_barracuda", "Piranhas and sharks are ugly! Soooo ugly! Did you know there's a fish that looks very pretty and still can eat your face off? I would pay 2 platinum to see that happen, by the way... To the point, though, you catchy for me. Just make sure I have it before you lose your face!(Caught Jungle Surface)", "食人鱼和鲨鱼都很丑！太太太丑了！你知道吗？有一种鱼长得很漂亮，但可以把你的脸咬掉。顺便说，我愿意付两铂金币来让这事发生。不过关键是，你要抓一条给我。一定要在你的脸被咬掉之前，把它交给我！（抓捕位置：丛林地表）")
                .text("item.confluence.tundra_trout", "You ever wonder why the lakes on the surface of the snowy areas of world never ice over? I don't. The fish, however, do! A fish made out of ice would make a great offering to the mighty and amazing Angler ! Go, my loyal subject, and bring me this Tundra Trout with haste!(Caught in Surface Tundra)", "你知道位于世界雪域表面上的湖泊为什么从不结冰吗？我不知道。但鱼知道！用冰制成的鱼会作为上佳的祭品进贡给伟大且神奇的渔夫！去吧，我的忠诚仆人，赶快把这条苔原鳟鱼带给我！（抓捕位置：地表苔原）")
                .text("item.confluence.unicorn_fish", "Unicorns and rainbows are absolutely great! They're everywhere, even in the water. No, really, I actually saw a unicorn fish in the Hallowed lake! Your job is to reel it up and let me have it as a pet!(Caught in Hallow)", "独角兽和彩虹都是好东西！它们无处不在，甚至在水中。真的，我居然在圣湖中看到一条独角兽鱼！你的任务就是把它钓起来，我会拿它当宠物！（抓捕位置：神圣之地）")
                .text("item.confluence.wyverntail", "I know something youuuuu don't! Fine, I'll tell you, there's a terrifying creature that flies among the stars! I'm not making this up! It's called a Wyvern! But, but, you knew that already, right? Well what you don't know is that they are born and raised as tadpoles! So, they're actually like.. well, a frog! Hop to it and get me one!(Caught in Sky Lakes)", "我知道一些你……你不知道的事！好吧，我告诉你，有一只可怕的怪物在星空中飞来飞去！这不是我自己编出来的！它叫飞龙！可是，可是，你已经知道了，是不是？你不知道的是，它们就像蝌蚪那样出生和成长！所以，它们实际上像……像青蛙！开始行动，给我抓一只！（抓捕位置：天湖）")
                .text("item.confluence.zombie_fish", "You won't believe it! I caught a fish in the forest at night that was already dead! Then it tried to eat me! I threw it away and ran! Now I want to stick it in someone's dresser to see what happens, so go fish it back up for me will ya?!(Caught in Surface)", "你相信吗？！我晚上在森林里抓到一条已经死了的鱼！然后，它想吃我！我把它扔掉，然后扭头就跑！现在，我要把它钉在别人的梳妆台上，看看会发生什么，所以你去把那条鱼给我抓回来，好吗？！（抓捕位置：地表）")
                .build());
        consumer.accept(NpcEntities.FEMALE_ANGLER, npc()
                .dialog("What a pity! Some fish may have disappeared even before I was born. I hope we can protect the remaining ones well~", "真可惜呀！有些鱼或许在我出生前就消失了，希望能好好保护剩下的它们～")
                .dialog("Ah... I'm so sorry! Did I disturb you while reeling in my line? I really apologize!", "啊……抱歉！是不是打扰到我收钓线了？实在不好意思呀！")
                .dialog("Although there's no chef in the whole %s, being able to cook fish with my own hands is a kind of happiness!", "整个%s里虽然没有厨师，但能自己亲手烹鱼，也是一种幸福呢！")
                .dialog("I don't have a mom or dad, but I have so many lovely fish to keep me company! I'm already very content~", "我没有爸爸妈妈，但有这么多可爱的鱼儿作伴！我已经很满足啦～")
                .dialog("Take my advice, sweetie~ Never touch ice cubes with your tongue! You'll get frostbite, so make sure to take good care of yourself!", "听姐姐一句劝哦，千万别用舌头碰冰块啦！会冻伤的，一定要好好照顾自己呀～")
                .dialog("Have you ever heard of fish that can make sounds? I think I've seen some before! Would you like to talk about it with me?", "你听说过会叫的鱼吗？我好像见过哦，要不要和我聊聊呀！")
                .dialog("Oh dear! Be careful~ I set up some little decorations, not traps! I just wanted to give everyone a small surprise, please don't misunderstand~", "哎呀！小心一点～我布置了些小装饰，不是陷阱啦，只是想给大家一点小惊喜，千万别误会哦～")
                .dialog("The %s is full of lovely and unique fish everywhere! It's truly wonderful!", "%s里到处都是可爱又特别的鱼儿，真的太美好了！")
                .text("stat.0", "You know what? I've already collected %s beautiful fish! Thank you so much—I could never have done it without your help!", "你知道吗？我已经收集到%s条超美的鱼了！真的太谢谢你了，没有你的帮忙我肯定做不到的！")
                .text("stat.1", "Oh no! I've bothered you %s times already, I'm so terribly sorry! But every time I get to see such beautiful fish, it makes me so happy!", "哎呀！麻烦你%s次了，真的太不好意思啦！不过每次都能见到这么好看的鱼，真的太开心了！")
                .text("wakeup.0", "Thank you so much! I really don't know how to repay you—you're an amazing friend!", "太感谢你了！真的不知道该怎么报答你，你真的是个超级好的朋友！")
                .text("wakeup.1", "Oh! It's you~ I just slipped a moment ago, I wasn't drowning! Thank you for caring so much!", "呀？是你呀～刚刚只是脚滑了一下，不是溺水啦，谢谢你这么关心我！")
                .text("wakeup.2", "Thank you for saving me! You're such a kind person~ If you don't mind, I'd like to ask you to help me with a small errand—but it's totally fine if you don't want to!", "谢谢你救了我！你真的太好了～如果你不介意的话，我想请你帮我做点小事，当然不愿意也没关系的！")
                .text("task_ready.0", "Hello there~ I have a tiny favor to ask. Could you help me if you're free? It's absolutely fine if you're not!", "你好呀～我有个小小的请求，如果你方便的话能不能帮我一下？不方便也完全没关系的！")
                .text("task_ready.1", "I'm looking for a special kind of fish. It would mean the world to me if you'd help find it! No rush at all—let's take our time to go over the details~", "我想找一条特别的鱼，如果你愿意帮忙找找的话就太好啦！不急的，慢慢说细节～")
                .text("task_ready.2", "%1$s would like to formally invite you to become %2$s's fishing helper! Would you accept?", "%1$s想正式邀请你成为%2$s的钓鱼小帮手，你愿意吗？")
                .text("task_ready.3", "Hello! You must be the amazing fishing master I've been looking for! Could I ask for your help, please?", "你好呀！你就是我一直想找的、超厉害的钓鱼大师吧！能请你帮帮忙吗？")
                .text("task_succeed.0", "Wow! Thank you for catching the fish I wanted—thank you so much! You must be tired, take it easy~", "哇！谢谢你帮我抓到想要的鱼，真的太感谢了！辛苦你啦～")
                .text("task_succeed.1", "You did an absolutely wonderful job! You've worked hard—go rest for a bit~", "你真的做得超棒的！辛苦你了，快歇一歇吧～")
                .text("task_succeed.2", "I'm so happy! You finished safely, that's such a relief! I was worried about you the whole time!", "太开心啦！你安全完成了，真的太好了！我还一直担心你呢！")
                .text("task_succeed.3", "Oh my goodness! You not only completed the task but also came back safe and sound—you're incredible! Please hand me the fish, and go get some rest~", "天呐！？你不仅完成了任务，还平平安安的，真的太棒了！快把鱼给我，快去休息一下吧～")
                .text("task_succeed.4", "We got it! Everything went perfectly~ Thank you for your help, I really appreciate it!", "抓到啦！一切都超顺利的～谢谢你的帮忙，真的太感谢了！")
                .text("task_finished.0", "I have enough fish now! Thank you so much for all your help—you've done so much for me~", "我的鱼已经足够啦！真的太谢谢你的帮忙，你真的帮了我太多了～")
                .text("task_finished.1", "Thank you so much for today! I had such a lovely time with you! Go take care of your own things now~", "今天真的谢谢你啦，和你相处超开心的！你快去忙自己的事吧～")
                .text("task_finished.2", "I don't have any errands for you right now, but thank you so much for stopping by anyway~", "暂时没有需要帮忙的啦，不过还是谢谢你愿意过来～")
                .text("task_finished.3", "I have enough fish for today—you've worked hard! Take care, and goodbye for now~", "今天的鱼已经够啦，辛苦你啦，慢走哦～")
                .text("task_finished.4", "I still have the fish you gave me last time safely stored away. I don't need more for now, but thank you so much all the same!", "上次你给我的鱼我还好好收着呢，暂时不需要啦，不过还是超感谢你的！")
                .text("task_finished.5", "Thank you so much! Having a helper like you is such a blessing to %s~", "真的太谢谢你啦！能有你这样的帮手，是%s的幸运呀～")
                .build());
        consumer.accept(NpcEntities.ZOOLOGIST, npc()
                .dialog("I collected critters like you once, then I took a cursed fox bite to the knee!", "我以前也像你一样养小动物，后来被一只受诅咒的狐狸咬到了膝盖！")
                .dialog("I may not know, like, a whole lot...but I can talk your head off about nature and critters and animals and wildlife and...", "我可能学识浅薄……但有关自然、小动物、动物和野生动物之类的话题，我可以给你说上三天三夜……")
                .dialog("My older bro calls me a lycanthrope. It means I'm like, part animal or something. He'd know, though, because he spends all his time outside!", "我老哥叫我兽化人。意思是我有一半像动物什么的。不过他都懂，毕竟他常年在外！")
                .dialog("I love animals, like, a lot! I tried to pet this weird looking fox one time, he sooo bit me, and now I became like one! Rad!", "我非常喜欢动物！有一次我试着抚摸这只长相奇特的狐狸，他竟然咬了我一口，现在我变成了一只动物！酷毙了！")
                .dialog("Staahp pulling on my tail, bro, it's totally real, and like...totally hurts when you pull on it!", "别拉扯我的尾巴了，兄弟，是货真价实的……这样拉扯，我很疼的！")
                .dialog("Oh, THESE ears? Haha, totes better to hear you with, my dear!", "噢，这两只大耳朵吗？哈哈，是用来听你的，宝贝！")
                .dialog("This one time, at critter camp, I woke up one morning and everything was torn apart! Like, wow, how did I sleep through THAT?!", "这次是在小动物营地，有天早上我醒来，发现一切全变样了！发生这么大的事情，我竟然还睡得着？！")
                .dialog("Wow, like, I've never seen a full moon. For some reason, it's like I pass out every time one's around!", "哇，我好像从来没见过满月。不知道为什么，一要出现满月我就会昏倒！")
                .dialog("I have noooo idea how I got here, but it's mega rad.", "我不知道我是怎么来到这里的，但这感觉太棒了。")
                .build());
        consumer.accept(NpcEntities.DRYAD, npc()
                .dialog("Stay safe! Both worlds need you!", "注意安全！两边的世界都需要你！")
                .dialog("The hourglass of time is slowly running out. And you're not aging gracefully.", "时间的沙漏在缓缓流逝。而你并没有优雅地变老。")
                .dialog("Two goblins walked into a bar, and one of them said to the other: 'A glass of beer?'", "两个哥布林走进酒吧，其中一个对另一个说：“来杯啤酒？！")
                .dialog("What does it mean by saying I'm all talk and no action?", "说我雷声大雨点小是啥意思？")
                .dialog("You must stop the spread of evil.", "你必须停止邪恶的蔓延。")
                .dialog("This world is much vaster... And the power of nature is stronger too.", "这个世界更为广阔……自然的力量也更强大了")
                .build());
        consumer.accept(NpcEntities.PAINTER, npc()
                .dialog("I know the difference between turquoise and teal. But I'm not going to tell you.", "我知道青绿色和蓝绿色之间的差别。但我不会告诉你。")
                .dialog("The titanium white is all used up. Don't ask.", "钛白色用完了，别问了。")
                .dialog("Try mixing pink and purple. It'll definitely work, I swear!", "尝试调合粉色和紫色，肯定管用，我发誓！")
                .dialog("No, no, no... There are many kinds of gray! Don't make me start...", "不、不、不……灰色也分很多种！别让我开始……")
                .dialog("I hope it stops raining. The paint still hasn't dried. It would be a disaster if it rains!", "我希望别下雨了，漆还没干。下雨就惨了！")
                .dialog("I tried organizing a paintball war, but everyone just wanted food and decorations.", "我试过举办一次彩弹大战，但是每个人都只想要食物和装饰品。")
                .build());
        consumer.accept(NpcEntities.ARMS_DEALER, npc()
                .dialog("Dude, get your hands off my gun!", "哥们，把手从我的枪上拿开！")
                .dialog("Hey, bro, this isn't a movie. You need to prepare ammunition separately.", "嘿，兄弟，这可不是演电影。需要另行准备弹药。")
                .dialog("I see you're eyeing the Minishark... You can't even imagine how it's made.", "我看你在盯着迷你鲨……你绝对想不到它是怎么做成的。")
                .dialog("I want to buy something from the Nurse. What did you say? She doesn't sell anything?", "我想买护士卖的东西。你说啥？她什么也不卖？")
                .dialog("Flying Fish? I call it target practice!", "飞鱼？我把它叫作打靶！")
                .dialog("Don't waste your time with the Demolitionist. I've got everything you need right here.", "别和爆破专家浪费时间了。我这边有你要的一切。")
                .build());
        consumer.accept(NpcEntities.STYLIST, npc()
                .dialog("Your hair is fine, but I can make it fabulous.", "你的发型还算过得去，不过我能让它变得惊艳。")
                .dialog("A fresh style can make even an adventurer look civilized.", "换个新造型，就连冒险者也能显得体面些。")
                .dialog("Sit still. Great hair takes precision.", "坐稳了。好发型需要精准的手艺。")
                .build());
        consumer.accept(NpcEntities.GOBLIN_TINKERER, npc()
                .dialog("Goblins get angry so easily. In fact, they can start a war over some rags!", "哥布林太容易生气了。事实上，他们能为了一些破布发动战争！")
                .dialog("To be honest, most goblins aren't real rocket scientists. Well, some of them are.", "老实说，大部分哥布林都不是真正的火箭科学家。好吧，有一些是。")
                .dialog("Do you know why everyone carries these spiky balls around? Because I don't.", "你知不知道为什么大家到哪儿都带着这些尖刺球？因为我不知道。")
                .dialog("I've just finished my latest creation! This version won't explode violently even if you blow or suck on it really hard.", "我刚刚完成了最新的作品！这个版本就算你对着它猛力吹吸也不会猛烈爆炸。")
                .dialog("Goblin thieves aren't very good at stealing. They can't even steal from an unlocked chest!", "哥布林盗贼不太擅长偷东西。没上锁的箱子都不会偷！")
                .dialog("Yo, I heard you like rockets and running shoes, so I added some rockets to your running shoes.", "唷，我听说你喜欢火箭和跑鞋，所以我在你的跑鞋上加了一些火箭。")
                .build());
        consumer.accept(NpcEntities.WITCH_DOCTOR, npc()
                .dialog("Which doctor am I? The Witch Doctor am I.", "我是啥医？我是巫医。")
                .dialog("Choose wisely, my commodities are volatile and my dark arts, mysterious.", "认真选，我的商品不稳定，我的黑魔法很神秘。")
                .dialog("The heart of magic is nature. The nature of hearts is magic.", "魔法的心脏是本质。心脏的本质是魔法。")
                .dialog("I sense a kindred spirit in the Etherian Dark Mages. A pity they are our enemies, I would have liked to learn from them.", "我感觉与埃特尼亚黑暗魔法师志趣相投。可惜他们是我们的敌人，我本来希望向他们学习来着。")
                .build());
        consumer.accept(NpcEntities.CLOTHIER, npc()
                .dialog("Thanks again for freeing me from my curse. Felt like something jumped up and bit me.", "再次感谢你帮我解了诅咒。感觉像是什么东西跳起来咬了我一口。")
                .dialog("Mama always said I would make a great tailor.", "妈妈总是说我会成为一位伟大的裁缝。")
                .dialog("Life's like a box of clothes; you never know what you are gonna wear!", "生活就像一箱衣服；你永远也不知道自己要穿什么！")
                .dialog("Of course embroidery is hard! If it wasn't hard, no one would do it! That's what makes it great.", "刺绣当然难了！如果不难，就没人绣了！所以刺绣是件难能可贵的事。")
                .dialog("I know everything they is to know about the clothierin' business.", "他们想了解服装行业，而我无所不知。")
                .dialog("Being cursed was lonely, so I once made a friend out of leather. I named him Wilson.", "被诅咒后很孤独，于是我用皮革制作了一个朋友。我叫他威尔森。")
                .dialog("I keep having vague memories of tying up a woman and throwing her in a dungeon.", "我依稀记得把一个女人捆了起来，然后扔到了地牢里。")
                .build());
        consumer.accept(NpcEntities.MECHANIC, npc()
                .dialog("Did you make sure your device was plugged in?", "你确定你的设备插好电源了？")
                .dialog("Oh, you know what this house needs? More blinking lights.", "哦，你知道这个房屋需要什么？需要更多的闪光信号灯。")
                .dialog("DON'T MOVE. I DROPPED MY CONTACT.", "别动。我的隐形眼镜掉了。")
                .dialog("Thank you! Sooner or later, I'll end up like the other skeletons in the dungeon.", "谢谢！迟早有一天，我的结局也会和地牢里的其他骷髅一样。")
                .dialog("I don't quite remember what happened in there. Three, maybe four important things...", "我记不太清楚里面发生过什么了，三个还是四个很重要的东西...")
                .dialog("Oh yes, the Signal Adapter! It can connect the redstone here to the wires perfectly.", "噢是的，信号适配器！它可以很好地把这里的红石和电线连接起来。")
                .build());
        consumer.accept(NpcEntities.PARTY_GIRL, npc()
                .dialog("We have to talk. It's... it's about parties.", "我们得谈谈。这……聚会的事。")
                .dialog("I can't decide what I like more: parties, or after-parties.", "我不知道我是更喜欢派对还是余兴派对。")
                .dialog("We should set up a blinkroot party, and we should also set up an after-party.", "我们应该办一个闪耀根派对，而且我们还应该办一个余兴派对。")
                .dialog("Put up a disco ball and then I'll show you how to party.", "装个迪斯科球，我会让你知道怎么开派对。")
                .dialog("I went to Sweden once, they party hard, why aren't you like that?", "我去过一次瑞典，他们经常开派对，你怎么和他们不一样？")
                .dialog("My name's Party Girl but people call me party pooper. Yeah I don't know, it sounds cool though.", "我叫派对女孩，但人们叫我派对扫把星。我也搞不懂为啥这样叫我，但听起来酷酷的。")
                .dialog("Do you party? Sometimes? Hm, okay then we can talk...", "你开派对吗？有时开？好吧，那我们谈谈……")
                .build());
        consumer.accept(NpcEntities.WIZARD, npc()
                .dialog("Want me to pull a coin from behind your ear? No? Ok.", "想让我从你耳朵后面掏出一个钱币吗？不想？好吧。")
                .dialog("Do you want some magic candy? No? Ok.", "想要一些魔法糖果吗？不想？好吧。")
                .dialog("I make a rather enchanting hot chocolate if you'd be inter...No? Ok.", "我做了一杯诱人的热巧克力，你感不感……不感兴趣？好吧。")
                .dialog("Are you here for a peek at my crystal ball?", "你来这里是不是想看看我的水晶球？")
                .dialog("Ever wanted an enchanted ring that turns rocks into slimes? Well neither did I.", "想不想要可以把石头变成史莱姆的魔戒？好吧，我也不想要。")
                .dialog("Someone once told me friendship is magic. That's ridiculous. You can't turn people into frogs with friendship.", "有人曾告诉我友谊是魔法。太荒谬了。你无法用友谊把人变成青蛙。")
                .dialog("I can see your future now... You will buy a lot of items from me!", "我现在能看到你的未来……你会从我这里买很多物品！")
                .dialog("I once tried to bring an Angel Statue to life. It didn't do anything.", "我曾经试过复活一座天使雕像。它啥都不干。")
                .build());
        consumer.accept(NpcEntities.TAX_COLLECTOR, npc()
                .dialog("Taxes are due. I assure you, every coin is accounted for.", "该交税了。我向你保证，每一枚钱币都记得清清楚楚。")
                .dialog("Running a town is expensive. Fortunately, everyone else pays for it.", "维持城镇可不便宜。幸运的是，账单由其他人来付。")
                .dialog("Another day, another ledger full of overdue payments.", "又是新的一天，账本里还是写满了欠款。")
                .build());
        consumer.accept(NpcEntities.TRUFFLE, npc()
                .dialog("As if living underground wasn't bad enough, jerks like you come in while I'm sleeping and steal my children.", "生活在地下已经够惨的了，像你这样的败类还要趁我睡觉来偷我的孩子。")
                .dialog("I tried to lick myself the other day to see what the big deal was, everything started glowing blue.", "有一天，我试着舔了舔自己，看看会发生什么大不了的事，然后全身都开始发蓝光。")
                .dialog("Everytime I see the color blue, it makes me depressed and lazy.", "每次看到蓝色，我都感到郁闷和懒散。")
                .dialog("You haven't seen any pigs around here have you? My brother lost his leg to one.", "你在这附近看到过猪吗？我弟弟的一条腿被猪叼走了。")
                .dialog("I don't know the 'Truffle Shuffle,' so stop asking!", "我不知道什么是“肚皮波浪”，所以别问了！")
                .dialog("There's been such a huge rumor that's being spread about me, 'If you can't beat him, eat him!'", "有个关于我的谣言正在盛传：“如果打不过他，那就吃掉他！")
                .dialog("I feel there are more of my kind here...", "我感觉这里有更多同类...")
                .build());
        consumer.accept(NpcEntities.STEAMPUNKER, npc()
                .dialog("Show me some gears!", "给我看看齿轮！")
                .dialog("Be it what it would, a jetpack would suit you nicely!", "不管怎么说，喷气背包一定很适合你！")
                .build());
        consumer.accept(NpcEntities.CYBORG, npc()
                .dialog("This sentence is false, or is it?", "这句话是假的，还是说它不是？")
                .dialog("Weapons systems online. Keep clear of the blast radius.", "武器系统已上线。请远离爆炸范围。")
                .build());
        consumer.accept(NpcEntities.NERDY_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.COOL_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.ELDER_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.CLUMSY_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.DIVA_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.SURLY_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.MYSTIC_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.SQUIRE_SLIME, npc().dialog("Blub.", "咕噜。").build());
        consumer.accept(NpcEntities.GOLFER, npc()
                .dialog("In this game, only losers go for that high score.", "在这项运动里，只有输家才追求高分。")
                .dialog("Thanks for finding me. That was quite a sand trap.", "谢谢你找到我，这个沙坑可真够深的。")
                .build());
        consumer.accept(NpcEntities.SKELETON_MERCHANT, npc()
                .dialog("No flesh, no fuss. Take a look at my wares.", "没皮没肉，倒也省心。看看我的货吧。")
                .dialog("The moon changes, and so does my stock.", "月相变了，我的货物也会变。")
                .build());
        consumer.accept(NpcEntities.TRAVELING_MERCHANT, npc()
                .dialog("Hmm, you look like you could use an Angel Statue! They slice, and dice, and make everything nice!", "嗯，看上去你会使用天使雕像！他们切片，又切丁，让一切都如此美好！")
                .dialog("I don't refund for \"buyer's remorse...\" Or for any other reason, really.", "我不会因“买家后悔……”或者任何其它原因退款，绝不退。")
                .dialog("Buy now and get free shipping!", "现在购买还包邮！")
                .dialog("I sell wares from places that might not even exist!", "我卖的商品来自可能根本就不存在的地方！")
                .dialog("You want two penny farthings!? Make it one and we have a deal.", "你想要两文钱！？一文钱就成交。")
                .dialog("Combination hookah and coffee maker! Also makes julienne fries!", "既能抽水烟，也能煮咖啡！还能炸切丝薯条！")
                .dialog("Come and have a look! One pound fish! Very, very good! One pound fish!", "看一看，瞧一瞧！一斤重的鱼！新鲜味美！一斤重的鱼！")
                .dialog("If you're looking for junk, you've come to the wrong place.", "如果你是在找垃圾，那就来错地方了。")
                .dialog("A thrift shop?  No, I am only selling the highest quality items on the market.", "旧货店？不，我只卖市场上的尖货。")
                .build());
        consumer.accept(NpcEntities.OLD_MAN, npc()
                .dialog("I cannot let you enter until you free me of my curse.", "如果你不解除我的诅咒，我是不会让你进的。")
                .dialog("Stranger, do you possess the strength to defeat my master?", "陌生人，你是否拥有能打败我主人的力量？")
                .dialog("Defeat my master, and I will grant you passage into the Dungeon.", "打败我的主人，我就让你进入地牢。")
                .dialog("Come back at night if you wish to enter.", "你要想进去的话就晚上再来。")
                .text("curse", "Curse", "诅咒")
                .build());
    }

    private final Path outputPath;

    public NPCDialogProvider(PackOutput output) {
        this.outputPath = output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve("confluence/npc/dialogs.json");
    }

    public static void addTranslations(BiConsumer<String, String> consumer, boolean english) {
        gather((entity, npc) -> {
            String prefix = prefix(entity);
            for (int index = 0; index < npc.dialogs().size(); index++) {
                DialogLine dialog = npc.dialogs().get(index);
                consumer.accept(prefix + index, english ? dialog.english() : dialog.chinese());
            }
            npc.texts().forEach(text -> consumer.accept(prefix + text.suffix(), english ? text.english() : text.chinese()));
        });
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        ImmutableMap.Builder<EntityType<?>, NPCDialog> builder = ImmutableMap.builder();
        Set<ResourceLocation> defined = new HashSet<>();
        gather((entity, npc) -> {
            validateDefinition(entity, npc, defined);
            builder.put(entity.get(), new NPCDialog(IntStream.range(0, npc.dialogs().size()).mapToObj(index -> prefix(entity) + index).toList()));
        });
        Set<ResourceLocation> registered = NpcEntities.ENTITIES.getEntries().stream().map(npc -> Objects.requireNonNull(npc.getId())).collect(Collectors.toSet());
        if (!registered.equals(defined))
            throw new IllegalStateException("NPC dialog definitions do not match registered NPC entities");
        DataResult<JsonElement> encoded = NPCDialog.MAP_CODEC.encodeStart(JsonOps.INSTANCE, builder.build());
        JsonElement json = encoded.result().orElseThrow(() -> new IllegalStateException("Unable to encode NPC dialogs: " + encoded.error().map(DataResult.PartialResult::message).orElse("unknown error")));
        return DataProvider.saveStable(output, json, outputPath);
    }

    private static void validateDefinition(RegistryObject<? extends EntityType<?>> entity, NPCDefinition npc, Set<ResourceLocation> defined) {
        if (!defined.add(Objects.requireNonNull(entity.getId())))
            throw new IllegalStateException("Duplicate NPC dialog definition: " + entity.getId());
        if (npc.dialogs().isEmpty())
            throw new IllegalStateException("No dialogs found for NPC: " + entity.getId());
        Set<String> suffixes = npc.texts().stream().map(NamedText::suffix).collect(Collectors.toSet());
        if (suffixes.size() != npc.texts().size())
            throw new IllegalStateException("Duplicate named NPC text: " + entity.getId());
    }

    private static String prefix(RegistryObject<? extends EntityType<?>> npc) {
        return "dialogs.confluence." + Objects.requireNonNull(npc.getId(), "NPC registry id is unavailable").getPath() + ".";
    }

    private static NPCDefinitionBuilder npc() {
        return new NPCDefinitionBuilder();
    }

    @Override
    public String getName() {
        return "Confluence NPC Dialogs";
    }

    private record NPCDefinition(List<DialogLine> dialogs, List<NamedText> texts) {}

    private record DialogLine(String english, String chinese) {}

    private record NamedText(String suffix, String english, String chinese) {}

    private static final class NPCDefinitionBuilder {
        private final List<DialogLine> dialogs = new ArrayList<>();
        private final List<NamedText> texts = new ArrayList<>();

        private NPCDefinitionBuilder dialog(String english, String chinese) {
            dialogs.add(new DialogLine(english, chinese));
            return this;
        }

        private NPCDefinitionBuilder text(String suffix, String english, String chinese) {
            texts.add(new NamedText(suffix, english, chinese));
            return this;
        }

        private NPCDefinition build() {
            return new NPCDefinition(List.copyOf(dialogs), List.copyOf(texts));
        }
    }
}
