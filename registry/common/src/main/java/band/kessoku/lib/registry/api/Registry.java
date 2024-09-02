/*
 * Copyright (c) 2024 KessokuTeaTime
 *
 * Licensed under the GNU Lesser General Pubic License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package band.kessoku.lib.registry.api;

import band.kessoku.lib.registry.impl.KessokuRegistryServices;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public interface Registry {
    static Registry getInstance() {
        return KessokuRegistryServices.getRegistry();
    }

    default <T> T register(net.minecraft.registry.Registry<? super T> registry, String id, T entry) {
        return register(registry, Identifier.of(id), entry);
    }

    <V, T extends V> T register(net.minecraft.registry.Registry<V> registry, Identifier id, T entry);

    default Item registerItem(Identifier id, Item.Settings settings) {
        return register(Registries.ITEM, id, new Item(settings));
    }

    default Item registerSimpleItem(Identifier id) {
        return register(Registries.ITEM, id, new Item(new Item.Settings()));
    }

    default Block registerBlock(Identifier id, AbstractBlock.Settings settings) {
        return register(Registries.BLOCK, id, new Block(settings));
    }

    default Block registerSimpleBlock(Identifier id) {
        return registerBlock(id, AbstractBlock.Settings.create());
    }

    default Item registerSimpleBlockItem(Identifier id, Block block) {
        return registerSimpleBlockItem(id, block, new Item.Settings());
    }

    default Item registerSimpleBlockItem(Identifier id, Block block, Item.Settings settings) {
        return register(Registries.ITEM, id, new BlockItem(block, settings));
    }

    default Item registerSimpleBlockItem(RegistryEntry<Block> block, Item.Settings settings) {
        return registerSimpleBlockItem(block.getKey().orElseThrow().getValue(), block.value(), settings);
    }

    default Item registerSimpleBlockItem(RegistryEntry<Block> block) {
        return registerSimpleBlockItem(block.getKey().orElseThrow().getValue(), block.value(), new Item.Settings());
    }
}