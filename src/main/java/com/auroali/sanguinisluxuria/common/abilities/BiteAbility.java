package com.auroali.sanguinisluxuria.common.abilities;

import com.auroali.sanguinisluxuria.VampireHelper;
import com.auroali.sanguinisluxuria.common.components.VampireComponent;
import com.auroali.sanguinisluxuria.common.registry.BLDamageSources;
import com.auroali.sanguinisluxuria.common.registry.BLParticles;
import com.auroali.sanguinisluxuria.common.registry.BLStatusEffects;
import com.auroali.sanguinisluxuria.common.registry.BLVampireAbilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;

public class BiteAbility extends VampireAbility implements EntitySyncableVampireAbility<LivingEntity> {
    @Override
    public void activate(LivingEntity entity, VampireComponent component) {
        if (component.getAbilties().isOnCooldown(this) || VampireHelper.isMasked(entity))
            return;

        HitResult result = VampireHelper.raycastEntity(entity, entity.getRotationVector(), Entity::isLiving);
        if (result.getType() != HitResult.Type.ENTITY)
            return;

        LivingEntity target = ((EntityHitResult) result).getEntity() instanceof LivingEntity e ? e : null;
        if (target == null)
            return;

        target.damage(BLDamageSources.bite(entity), 3);
        target.addStatusEffect(new StatusEffectInstance(BLStatusEffects.BLEEDING, 100, 0));
        this.sync(entity, target);
        if (component.getAbilties().hasAbility(BLVampireAbilities.INFECTIOUS)) {
            SyncableVampireAbility.syncAbility(entity, BLVampireAbilities.INFECTIOUS, InfectiousAbility.InfectiousData.create(target, entity.getStatusEffects()));
            VampireHelper.transferStatusEffects(entity, target);
        }
        component.getAbilties().setCooldown(this, 220);
    }

    @Override
    public void handle(LivingEntity entity, LivingEntity data) {
        Box box = data.getBoundingBox();
        Random rand = data.getRandom();
        int max = 15;
        for (int i = 0; i < max; i++) {
            double x = box.minX + rand.nextDouble() * box.getXLength();
            double y = box.minY + rand.nextDouble() * box.getYLength();
            double z = box.minZ + rand.nextDouble() * box.getZLength();
            data.getWorld().addParticle(
              BLParticles.FALLING_BLOOD,
              x,
              y,
              z,
              0,
              0,
              0
            );
        }
    }
}
