package me.ichun.mods.hats.common.hats.sort;

import com.google.common.collect.Ordering;
import me.ichun.mods.hats.common.world.HatsSavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class SorterCount extends HatSorter
{
    @Override
    @Nonnull
    public String type()
    {
        return "sorterCount";
    }

    @Override
    public void sort(ArrayList hats)
    {
        TreeMap<Integer, ArrayList<HatsSavedData.HatPart>> hatsByCount = new TreeMap<>(Comparator.naturalOrder());

        for(Object o : hats)
        {
            HatsSavedData.HatPart hat = (HatsSavedData.HatPart)o;

            hatsByCount.computeIfAbsent(hat.count, k -> new ArrayList<>()).add(hat);
        }

        hats.clear();

        hatsByCount.forEach((k, v) -> hats.add(v));
        if(isInverse)
        {
            Collections.reverse(hats);
        }
    }
}
