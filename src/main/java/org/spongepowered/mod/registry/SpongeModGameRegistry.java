/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.mod.registry;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraftforge.fml.common.registry.GameData;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.common.entity.SpongeEntityType;
import org.spongepowered.common.registry.RegistryHelper;
import org.spongepowered.common.registry.SpongeGameRegistry;

import java.util.Map;

@NonnullByDefault
public class SpongeModGameRegistry extends SpongeGameRegistry {

    @Override
    public GameDictionary getGameDictionary() {
        return SpongeGameDictionary.instance;
    }

    @Override
    public void postInit() {
        super.postInit();
        setBlockTypes();
        setItemTypes();
    }

    public java.util.Optional<BlockType> getBlock(String id) {
        if (!id.contains(":")) {
            id = "minecraft:" + id; // assume vanilla
        }
        return java.util.Optional.ofNullable((BlockType) GameData.getBlockRegistry().getObject(id));
    }

    public java.util.Optional<ItemType> getItem(String id) {
        if (!id.contains(":")) {
            id = "minecraft:" + id; // assume vanilla
        }
        return java.util.Optional.ofNullable((ItemType) GameData.getItemRegistry().getObject(id));
    }

    private void setBlockTypes() {
        RegistryHelper.mapFields(BlockTypes.class, fieldName -> {
            return getBlock(fieldName.toLowerCase()).get();
        });
    }

    private void setItemTypes() {
        RegistryHelper.mapFields(ItemTypes.class, fieldName -> {
            return getItem(fieldName.toLowerCase()).get();
        });
    }

    // used by EntityRegistry
    public void registerEntityType(String id, EntityType type) {
        this.entityTypeMappings.put(id, type);
        this.entityClassToTypeMappings.put(((SpongeEntityType) type).entityClass, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CatalogType> java.util.Optional<T> getType(Class<T> typeClass, String id) {
        Map<String, ? extends CatalogType> tempMap = this.catalogTypeMap.get(checkNotNull(typeClass, "null type class"));
        if (tempMap == null) {
            return java.util.Optional.empty();
        } else {
            if (BlockType.class.isAssignableFrom(typeClass) || ItemType.class.isAssignableFrom(typeClass)
                    || EntityType.class.isAssignableFrom(typeClass)) {
                if (!id.contains(":")) {
                    id = "minecraft:" + id; // assume vanilla
                }
            }

            T type = (T) tempMap.get(id.toLowerCase());
            if (type == null) {
                return java.util.Optional.empty();
            } else {
                return java.util.Optional.of(type);
            }
        }
    }

}
