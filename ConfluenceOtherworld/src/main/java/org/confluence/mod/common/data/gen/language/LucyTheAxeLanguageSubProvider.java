package org.confluence.mod.common.data.gen.language;

import java.util.function.BiConsumer;

/// 生成露西斧的双语对话文本；露西斧不是城镇 NPC，因此不属于 NPC 对话表。
public final class LucyTheAxeLanguageSubProvider {
    private static void gather(TextConsumer consumer) {
        consumer.accept("cutting_down_a_tree.0", "Again! Let's chop another!", "咱们再去砍棵树吧！");
        consumer.accept("cutting_down_a_tree.1", "This is AWESOME!", "这真是太棒了！");
        consumer.accept("cutting_down_a_tree.2", "Die! Die!", "去死！去死！");
        consumer.accept("cutting_down_a_tree.3", "This is what I live for!", "我活着就是为了这个！");
        consumer.accept("cutting_down_a_tree.4", "Stupid tree!", "愚蠢的树！");
        consumer.accept("cutting_down_a_tree.5", "I feel ALIVE!", "我活力四射！");
        consumer.accept("cutting_down_a_tree.6", "We did it!", "我们成功了！");
        consumer.accept("cutting_down_a_tree.7", "HaHAHA!", "哈哈哈！");
        consumer.accept("cutting_down_a_tree.8", "Woooooo!", "哇哦！");
        consumer.accept("cutting_down_a_tree.9", "YEEEESS!", "太好了！");
        consumer.accept("cutting_down_a_gem_tree.0", "So, stone trees... what's up with that?", "所以，石头树……那是怎么回事？");
        consumer.accept("cutting_down_a_cactus.0", "When you think about it, cacti are just prickly trees!", "你想想看，仙人掌就是带刺的树而已！");
        consumer.accept("placed_in_other_container.0", "This sucks.", "真是糟透了。");
        consumer.accept("placed_in_other_container.1", "It's dark in here!", "这里好黑！");
        consumer.accept("placed_in_other_container.2", "Help! Get me out!", "救命！快把我弄出去！");
        consumer.accept("placed_in_other_container.3", "There's nothing to chop in here!", "这里没什么可砍的！");
        consumer.accept("placed_in_other_container.4", "I'm claustrophobic!", "我有幽闭恐惧症！");
        consumer.accept("placed_back_into_the_inventory.0", "Oh, thank goodness!", "哦，谢天谢地！");
        consumer.accept("placed_back_into_the_inventory.1", "From now on we'll be inseparable.", "从现在开始，我们形影不离。");
        consumer.accept("placed_back_into_the_inventory.2", "Ahh, I'm back where I belong.", "啊，我回到了我该待的地方。");
        consumer.accept("idle.0", "Hey! Chop some trees!", "嘿！砍些树吧！");
        consumer.accept("idle.1", "This is perfect chopping weather.", "今天的天气非常适合砍树。");
        consumer.accept("idle.2", "Chop chop chop. Heh.", "砍砍砍。嘿嘿。");
        consumer.accept("idle.3", "We make a good team!", "我们合作得很好！");
        consumer.accept("idle.4", "Let's find a small grove and just go nuts!", "咱们找片小树林疯砍一通吧！");
        consumer.accept("throw_on_the_ground.0", "You'll come back, right?", "你会回来的对吧？");
        consumer.accept("throw_on_the_ground.1", "Hey! I'd never throw you away!", "嘿！我绝对不会丢下你的！");
        consumer.accept("throw_on_the_ground.2", "Don't forget about me!", "别把我给忘了！");
        consumer.accept("throw_on_the_ground.3", "Aren't we friends?", "我们不是朋友吗？");
        consumer.accept("throw_on_the_ground.4", "You're abandoning me?!", "你要抛弃我？！");
        consumer.accept("attack_entity.0", "Knock it off!", "踢掉它！");
        consumer.accept("attack_entity.1", "Rude!", "太没礼貌了！");
        consumer.accept("attack_entity.2", "I am an axe possessed!", "我是把中邪的斧头！");
        consumer.accept("attack_entity.3", "Pick on someone your own size!", "挑个和你相配的对手！");
        consumer.accept("attack_entity.4", "Hey!", "喂！");
        consumer.accept("attack_entity.5", "Ouch, that hurts.", "噢痛，那很痛。");
        consumer.accept("attack_entity.6", "Get rid of them! It's freaking me out.", "干掉它们！我被吓死了。");
        consumer.accept("attack_entity.7", "This is what I live for!", "这就是我生存的目的！");
        consumer.accept("attack_entity.8", "Woo-hoohoo!", "哇哈哈！");
        consumer.accept("attack_entity.9", "RAAAA!", "吼！");
        consumer.accept("attack_entity.10", "Die! Die!", "去死！去死！");
        consumer.accept("attack_entity.11", "Nice swing!", "漂亮的一斧！");
        consumer.accept("attack_entity.12", "This is AWESOME!", "太棒了！");
        consumer.accept("attack_entity.13", "We make a good team!", "我们是一对好搭档！");
        consumer.accept("attack_entity.14", "Leave me alone, you brute!", "别烦我，你这个禽兽！");
        consumer.accept("kill_entity.0", "OH YES!", "哦，太对了！");
        consumer.accept("kill_entity.1", "So juicy!", "多汁！");
        consumer.accept("kill_entity.2", "A satisfying mea!", "一顿满足！");
        consumer.accept("kill_entity.3", "Worthy!", "太值了！");
        consumer.accept("kill_entity.4", "Beautiful!", "漂亮！");
        consumer.accept("kill_entity.5", "A delight!", "令人高兴！");
        consumer.accept("kill_entity.6", "I am satiated!", "我很满足！");
        consumer.accept("kill_entity.7", "Gluttonous!", "狼吞虎咽！");
        consumer.accept("kill_entity.8", "I'm proud of us!", "我为我们感到骄傲！");
        consumer.accept("kill_entity.9", "That's it?", "就这？");
        consumer.accept("kill_entity.10", "AXE-IMUM POWER!!!", "斧头力量！！！");
        consumer.accept("kill_entity.11", "Too easy!", "太简单了！");
        consumer.accept("kill_entity.12", "This is AWESOME!", "砍得好！");
        consumer.accept("kill_entity.13", "You're a HACK!", "你是个砍客！");
        consumer.accept("destroy_wrong_block.0", "I'm rather insulted.", "我有点受到侮辱。");
        consumer.accept("destroy_wrong_block.1", "This sucks.", "这真没劲。");
        consumer.accept("destroy_wrong_block.2", "I prefer the tree-y variety.", "我更喜欢更树的品种。");
        consumer.accept("destroy_wrong_block.3", "you've changed!", "你变了！");
        consumer.accept("destroy_wrong_block.4", "Remember how much fun we had chopping trees?", "还记得我们砍树的乐事吗？");
        consumer.accept("destroy_wrong_block.5", "Hey! Chop some trees!", "喂！砍一些树！");
        consumer.accept("destroy_wrong_block.6", "All work and no play makes me want to chop some trees.", "只工作不娱乐让我想砍更多的树。");
        consumer.accept("destroy_wrong_block.7", "Do you know what chop means?", "你知道砍的意思吗？");
        consumer.accept("destroy_wrong_block.8", "There's nothing to chop in here!", "这里面没有一样能砍的东西！");
        consumer.accept("destroy_wrong_block.9", "This is honestly the worst.", "这真是最糟糕的情况。");
        consumer.accept("destroy_wrong_block.10", "I'm getting dirty!", "我身上都脏了！");
        consumer.accept("destroy_wrong_block.11", "How dare you do this to me!", "你怎么敢如此对我！");
        consumer.accept("destroy_wrong_block.12", "Ooo I'll remember that!", "哦，我会记住那的！");
        consumer.accept("destroy_wrong_block.13", "You don't have to do this!", "你不需要这样！");
        consumer.accept("destroy_wrong_block.14", "Are you sure you're feeling alright?", "你确定你还好吗？");
        consumer.accept("destroy_wrong_block.15", "You're looking extra gruff today.", "今天你看起来特别粗暴。");
        consumer.accept("destroy_wrong_block.16", "You're overdoing it!", "太过分了！");
    }

    public static void addTranslations(BiConsumer<String, String> consumer, boolean english) {
        gather((suffix, englishText, chineseText) -> consumer.accept("dialogs.confluence.lucy_the_axe." + suffix, english ? englishText : chineseText));
    }

    @FunctionalInterface
    private interface TextConsumer {
        void accept(String suffix, String english, String chinese);
    }
}
