package com.bpd.prayertime.content.application;

import java.util.Set;

public record CreateContentCommand(String title, TimeDto time, Set<NewItemCommand> resources) {}
