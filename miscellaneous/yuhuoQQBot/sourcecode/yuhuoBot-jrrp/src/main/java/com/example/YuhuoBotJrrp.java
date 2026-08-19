package com.example;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.*;
import sun.jvm.hotspot.ui.ObjectHistogramPanel;

import java.util.List;
import java.time.LocalDate;
import java.util.*;
import java.util.Collections;

class jrrpData {
    public long groupID;
    public long userID;
    public int jrrp;
}
public final class YuhuoBotJrrp extends JavaPlugin {
    public static final YuhuoBotJrrp INSTANCE = new YuhuoBotJrrp();


    private YuhuoBotJrrp() {
        super(new JvmPluginDescriptionBuilder("com.example.yuhuoBot-jrrp", "0.1.0")
                .name("yuhuoBot-jrrp")
                .author("FishyFiree")

                .build());
    }

    List<jrrpData> jrrpDataList = new ArrayList<>();
    String currentDate = LocalDate.now().toString();
    private void checkDateAndClear() {
        String date = LocalDate.now().toString();
        if (!date.equals(currentDate)) {
            currentDate = date;
            jrrpDataList.clear();
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");

        Listener<GroupMessageEvent> listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class,
                event -> {
                    MessageChain message = event.getMessage();
                    String messageString = message.contentToString().trim();
                    long groupID = event.getGroup().getId();
                    long userID = event.getSender().getId();
                    String date = LocalDate.now().toString();

                    if (messageString.startsWith("/jrrptop")) {
                        checkDateAndClear();

                        MessageChainBuilder builder = new MessageChainBuilder();
                        builder.append("本群("+groupID+")今日人品排行榜\n日期："+date+"\n\n");

                        List<jrrpData> groupJrrpData = new ArrayList<>();
                        for (jrrpData data : jrrpDataList) {
                            if (data.groupID == groupID) {
                                groupJrrpData.add(data);
                            }
                        }

                        Collections.sort(groupJrrpData, (a, b) -> Integer.compare(b.jrrp, a.jrrp));
                        if (groupJrrpData.size() == 0) {
                            builder.append("本群还没有人获取过今日人品哦~");
                        }
                        else {
                            int sum = 0;
                            for (int i = 1; i<=groupJrrpData.size(); i++) {
                                sum += groupJrrpData.get(i-1).jrrp;
                                builder.append(new PlainText(i + ". "))
                                        .append(event.getGroup().get(groupJrrpData.get(i-1).userID).getNick())
                                        .append(new PlainText("("+groupJrrpData.get(i-1).userID+"): "+groupJrrpData.get(i-1).jrrp+"\n"));
                            }
                            double avg = (double)sum / groupJrrpData.size();
                            builder.append(new PlainText("\n本群今日人品平均值: "+avg));
                        }

                        MessageChain chain = builder.build();
                        event.getGroup().sendMessage(chain);
                    }
                    else if (messageString.startsWith("/jrrp")) {
                        checkDateAndClear();

                        MessageChainBuilder builder = new MessageChainBuilder();
                        builder.append(new At(userID));
                        boolean obtained = false;
                        int jrrp=0;
                        for (jrrpData data : jrrpDataList) {
                            if (data.groupID == groupID && data.userID == userID) {
                                jrrp = data.jrrp;
                                obtained = true;
                            }
                        }

                        if (obtained) {
                            builder.append(new PlainText("\n您今日已获取过今日人品，人品值为："+jrrp));
                        }
                        else {
                            Random random = new Random();
                            jrrp = random.nextInt(101);
                            jrrpData newData = new jrrpData();
                            newData.userID = userID;
                            newData.groupID = groupID;
                            newData.jrrp = jrrp;
                            jrrpDataList.add(newData);
                            builder.append(new PlainText("\n您的今日人品为："+jrrp));
                        }

                        MessageChain chain = builder.build();
                        event.getGroup().sendMessage(chain);
                    }
                });
    }
}